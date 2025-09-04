package com.airbng.service;

import com.airbng.common.exception.ImageException;
import com.airbng.common.exception.LockerException;
import com.airbng.common.exception.MemberException;
import com.airbng.domain.Locker;
import com.airbng.domain.Member;
import com.airbng.domain.base.BaseStatus;
import com.airbng.domain.base.ReservationState;
import com.airbng.domain.image.Image;
import com.airbng.domain.image.LockerImage;
import com.airbng.domain.jimtype.JimType;
import com.airbng.domain.jimtype.LockerJimType;
import com.airbng.dto.jimType.JimTypeResult;
import com.airbng.dto.jimType.LockerJimTypeUpdateResult;
import com.airbng.dto.locker.*;
import com.airbng.repository.*;
import com.airbng.util.S3Utils;
import com.github.benmanes.caffeine.cache.Cache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.airbng.common.response.status.BaseResponseStatus.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LockerServiceImpl implements LockerService {

    private final LockerRepository lockerRepository;
    private final ImageRepository imageRepository;
    private final LockerImageRepository lockerImageRepository;
    private final LockerJimTypeRepository lockerJimTypeRepository;
    private final JimTypeRepository jimTypeRepository;
    private final MemberRepository memberRepository;
    private final S3Utils s3Utils;

    private final RedisTemplate<String, LockerTop5Response> top5RedisTemplate;
    private final Cache<String, LockerTop5Response> localCache;

    // ================= 검색 =================
    @Override
    public LockerSearchResponse findAllLockerBySearch(LockerSearchRequest request) {
        var ids = request.getJimTypeId();
        boolean emptyJimTypes = (ids == null || ids.isEmpty());

        var lockers = lockerRepository.searchForList(
                request.getAddress(), request.getLockerName(), ids, emptyJimTypes
        );
        if (lockers.isEmpty()) throw new LockerException(NOT_FOUND_LOCKER);

        long count = lockerRepository.searchCount(
                request.getAddress(), request.getLockerName(), ids, emptyJimTypes
        );

        var previews = lockers.stream().map(l -> {
            String url = l.getLockerImages() == null ? null :
                    l.getLockerImages().stream()
                            .sorted(Comparator.comparing(li -> li.getImage().getImageId()))
                            .map(li -> li.getImage().getUrl())
                            .findFirst().orElse(null);

            return LockerPreviewResult.builder()
                    .lockerId(l.getLockerId())
                    .lockerName(l.getLockerName())
                    .address(l.getAddress())
                    .latitude(l.getLatitude())
                    .longitude(l.getLongitude())
                    .isAvailable(String.valueOf(l.getIsAvailable()))
                    .url(url)
                    .jimTypeResults(
                            l.getLockerJimTypes() == null ? List.of() :
                                    l.getLockerJimTypes().stream()
                                            .map(x -> new JimTypeResult(
                                                    x.getJimType().getJimTypeId(),
                                                    x.getJimType().getTypeName()))
                                            .toList()
                    )
                    .build();
        }).toList();

        return LockerSearchResponse.builder()
                .count(count)
                .lockers(previews)
                .build();
    }

    // ================= 상세 =================
    @Override
    public LockerDetailResponse findLockerById(Long lockerId) {
        Locker locker = lockerRepository.findLockerById(lockerId)
                .orElseThrow(() -> new LockerException(NOT_FOUND_LOCKERDETAILS));
        return LockerDetailResponse.from(locker);
    }

    // ================= 내 보관소 상세 =================
    @Override
    public LockerDetailResponse findMyLocker(Long memberId) {
        Locker locker = lockerRepository.findMyLocker(memberId)
                .orElseThrow(() -> new LockerException(NOT_FOUND_LOCKERDETAILS));
        return LockerDetailResponse.from(locker);
    }

    // ================= Top5 =================
    @Override
    public LockerTop5Response findTop5Locker() {
        LockerTop5Response cached = localCache.getIfPresent("lockerTop5");
        if (cached != null) return cached;

        LockerTop5Response redisValue = top5RedisTemplate.opsForValue().get("lockerTop5");
        if (redisValue != null) {
            localCache.put("lockerTop5", redisValue);
            return redisValue;
        }

        var lockers = lockerRepository.findTop5LockersByReservation(ReservationState.COMPLETED);
        var response = LockerTop5Response.from(lockers);

        top5RedisTemplate.opsForValue().set("lockerTop5", response, 1, TimeUnit.HOURS);
        localCache.put("lockerTop5", response);
        top5RedisTemplate.convertAndSend("lockerTop5Updated", "invalidate");
        return response;
    }

    // ================= 등록 =================
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void registerLocker(LockerInsertRequest dto) throws IOException {
        if (lockerRepository.existsByKeeper_MemberId(dto.getKeeperId())) {
            throw new LockerException(MEMBER_ALREADY_HAS_LOCKER);
        }
        if (!memberRepository.existsById(dto.getKeeperId())) {
            throw new MemberException(NOT_FOUND_MEMBER);
        }

        Member keeper = memberRepository.findById(dto.getKeeperId())
                .orElseThrow(() -> new MemberException(NOT_FOUND_MEMBER));

        Locker locker = Locker.builder()
                .lockerName(dto.getLockerName())
                .isAvailable(dto.getIsAvailable())
                .address(dto.getAddress())
                .addressEnglish(dto.getAddressEnglish())
                .addressDetail(dto.getAddressDetail())
                .latitude(dto.getLatitude())
                .longitude(dto.getLongitude())
                .keeper(keeper)
                .reservationCount(0L)
                .status(BaseStatus.ACTIVE)
                .build();

        lockerRepository.saveAndFlush(locker);

        // 이미지 저장/연결
        if (dto.getImages() != null && !dto.getImages().isEmpty()) {
            if (dto.getImages().size() > 5) throw new ImageException(EXCEED_IMAGE_COUNT);

            for (MultipartFile file : dto.getImages()) {
                if (file.isEmpty()) throw new ImageException(EMPTY_FILE);

                String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
                String path = "lockers/" + fileName;

                String url;
                try { url = s3Utils.upload(file, path); }
                catch (IOException e) { throw new ImageException(UPLOAD_FAILED); }

                Image image = Image.builder()
                        .url(url)
                        .uploadName(file.getOriginalFilename())
                        .status(BaseStatus.ACTIVE)
                        .build();
                imageRepository.save(image);

                lockerImageRepository.save(
                        LockerImage.builder().locker(locker).image(image).status(BaseStatus.ACTIVE).build()
                );
            }
        }

        // 짐타입 연결
        List<Long> jimTypeIds = dto.getJimTypeIds();
        if (jimTypeIds != null && !jimTypeIds.isEmpty()) {
            if (new HashSet<>(jimTypeIds).size() != jimTypeIds.size())
                throw new LockerException(DUPLICATE_JIMTYPE);

            List<Long> valid = jimTypeRepository.findValidIds(jimTypeIds);
            if (valid.size() != jimTypeIds.size())
                throw new LockerException(INVALID_JIMTYPE);

            List<JimType> types = jimTypeRepository.findAllById(jimTypeIds);
            for (JimType t : types) {
                lockerJimTypeRepository.save(
                        LockerJimType.builder().locker(locker).jimType(t).status(BaseStatus.ACTIVE).build()
                );
            }
        }
    }

    // ================= 활성화 토글 =================
    @Transactional
    @Override
    public void updateLockerActivation(Long lockerId) {
        Locker locker = lockerRepository.findById(lockerId)
                .orElseThrow(() -> new LockerException(NOT_FOUND_LOCKER));
        locker.updateIsAvailable(); // 더티체킹
    }

    // ================= 존재 여부 =================
    @Override
    public boolean isExistLocker(Long memberId) {
        return lockerRepository.existsByKeeper_MemberId(memberId);
    }

    // ================= 수정화면(특정 locker) =================
    @Override
    public LockerUpdateResponse findUpdateUserById(Long lockerId) {
        Locker locker = lockerRepository.findLockerById(lockerId)
                .orElseThrow(() -> new LockerException(NOT_FOUND_LOCKERDETAILS));

        List<LockerJimTypeUpdateResult> all = jimTypeRepository.findAll().stream()
                .map(j -> {
                    var r = new LockerJimTypeUpdateResult();
                    r.setJimTypeId(j.getJimTypeId());
                    r.setTypeName(j.getTypeName());
                    r.setPricePerHour(j.getPricePerHour());
                    boolean enabled = locker.getLockerJimTypes().stream()
                            .anyMatch(x -> x.getJimType().getJimTypeId().equals(j.getJimTypeId()));
                    r.setEnabled(enabled);
                    return r;
                }).toList();

        LockerUpdateResponse resp = LockerUpdateResponse.from(locker);
        resp.setImages(locker.getLockerImages().stream().map(li -> li.getImage().getUrl()).toList());
        resp.setJimTypeResults(all);
        return resp;
    }

    // ================= 수정화면(내 보관소) =================
    @Override
    public LockerUpdateResponse findUpdateMyLocker(Long memberId) {
        Locker locker = lockerRepository.findMyLocker(memberId)
                .orElseThrow(() -> new LockerException(NOT_FOUND_LOCKERDETAILS));
        LockerUpdateResponse resp = LockerUpdateResponse.from(locker);
        resp.setImages(locker.getLockerImages().stream().map(li -> li.getImage().getUrl()).toList());
        return resp;
    }

    // ================= 업데이트 =================
    @Transactional
    @Override
    public void updateLocker(Long keeperId, LockerUpdateRequest dto, List<MultipartFile> images) throws IOException {

        Locker locker = lockerRepository.findById(dto.getLockerId())
                .orElseThrow(() -> new LockerException(NOT_FOUND_LOCKER));

        // 권한 체크: 현재 로그인한 사용자(keeperId)가 이 락커의 소유자인지 확인
        Long ownerId = locker.getKeeper().getMemberId();
        if (!Objects.equals(ownerId, keeperId)) {
            throw new MemberException(NOT_FOUND_MEMBER); // 적절한 에러코드 사용
        }

        // 기본 정보 업데이트
        locker.setLockerName(dto.getLockerName());
        locker.setIsAvailable(dto.getIsAvailable());
        locker.setAddress(dto.getAddress());
        locker.setAddressEnglish(dto.getAddressEnglish());
        locker.setAddressDetail(dto.getAddressDetail());
        locker.setLatitude(dto.getLatitude());
        locker.setLongitude(dto.getLongitude());

        // 이미지 교체(이미지 전달된 경우에만 모두 교체)
        if (images != null && !images.isEmpty()) {
            lockerRepository.deleteLockerImagesByLockerId(locker.getLockerId());

            for (MultipartFile file : images) {
                if (file == null || file.isEmpty()) continue;

                String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
                String path = "lockers/" + fileName;
                String url = s3Utils.upload(file, path);

                Image image = Image.builder()
                        .url(url)
                        .uploadName(file.getOriginalFilename())
                        .status(BaseStatus.ACTIVE)
                        .build();
                imageRepository.save(image);

                lockerImageRepository.save(
                        LockerImage.builder().locker(locker).image(image).status(BaseStatus.ACTIVE).build()
                );
            }
        }

        // 짐타입 재연결(리스트가 넘어온 경우에만)
        if (dto.getJimTypeIds() != null) {
            lockerRepository.deleteLockerJimTypesByLockerId(locker.getLockerId());

            List<JimType> types = dto.getJimTypeIds().stream()
                    .filter(Objects::nonNull)
                    .map(id -> jimTypeRepository.findById(id)
                            .orElseThrow(() -> new LockerException(INVALID_JIMTYPE)))
                    .toList();

            for (JimType t : types) {
                lockerJimTypeRepository.save(
                        LockerJimType.builder().locker(locker).jimType(t).status(BaseStatus.ACTIVE).build()
                );
            }
        }
    }

    // ================= 삭제 =================
    @Transactional
    @Override
    public void deleteLocker(Long lockerId) {
        if (!lockerRepository.existsById(lockerId))
            throw new LockerException(NOT_FOUND_LOCKER);

        lockerRepository.deleteLockerImagesByLockerId(lockerId);
        lockerRepository.deleteLockerJimTypesByLockerId(lockerId);
        lockerRepository.deleteById(lockerId);
    }
}
