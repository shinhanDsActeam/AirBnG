package com.airbng.service;

import com.airbng.common.exception.ImageException;
import com.airbng.common.exception.LockerException;
import com.airbng.common.exception.MemberException;
import com.airbng.domain.Locker;
import com.airbng.domain.Member;
import com.airbng.domain.base.ReservationState;
import com.airbng.domain.image.Image;
import com.airbng.dto.jimType.LockerJimTypeUpdateResult;
import com.airbng.dto.locker.*;
import com.airbng.mappers.LockerMapper;
import com.airbng.repository.LockerRepository;
import com.airbng.util.S3Utils;
import com.github.benmanes.caffeine.cache.Cache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.airbng.common.response.status.BaseResponseStatus.*;


@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LockerServiceImpl implements LockerService {

    private final LockerMapper lockerMapper;
    private final LockerRepository lockerRepository;
    private final S3Utils s3Utils;

    private final Cache<String, LockerTop5Response> localCache;

    @Override
    public LockerSearchResponse findAllLockerBySearch(LockerSearchRequest request) {
        log.info("LockerServiceImpl.findAllLockerBySearch");
        List<LockerPreviewResult> lockers = lockerMapper.findAllLockerBySearch(request);
        if (lockers.isEmpty()) throw new LockerException(NOT_FOUND_LOCKER);

        LockerSearchResponse response = LockerSearchResponse.builder()
                .count(lockerMapper.findLockerCount(request))
                .lockers(lockers)
                .build();

        return response;
    }

    @Override
    public LockerDetailResponse findLockerById(Long lockerId) {
        Locker locker = lockerRepository.findLockerById(lockerId)
                .orElseThrow(() -> new LockerException(NOT_FOUND_LOCKERDETAILS));
        return LockerDetailResponse.from(locker);

    }

    @Override
    public LockerDetailResponse findMyLocker(Long memberId) {
        LockerDetailResponse result = lockerMapper.findLockerDetailByMemberId(memberId);
        if (result == null) {
            throw new LockerException(NOT_FOUND_LOCKERDETAILS);
        }

        result.setImages(lockerMapper.findImageById(result.getLockerId()));
        return result;
    }

    @Override
    public LockerTop5Response findTop5Locker() {
        return localCache.get("lockerTop5", key -> {
            List<Locker> lockers = lockerRepository
                    .findTop5LockersByReservation(ReservationState.COMPLETED);
            return LockerTop5Response.from(lockers);
        });
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void registerLocker(LockerInsertRequest dto) throws IOException {
        log.info("서비스 객체");

        log.info("보관소 존재 여부: {}", lockerMapper.findLockerByMemberId(dto.getKeeperId()));

        if (lockerMapper.findLockerByMemberId(dto.getKeeperId()) > 0) {
            log.info("예외처리");
            throw new LockerException(MEMBER_ALREADY_HAS_LOCKER);
        }

        if (lockerMapper.findMemberId(dto.getKeeperId()) == 0) {
            throw new MemberException(NOT_FOUND_MEMBER);
        }

        Locker locker = Locker.builder()
                .lockerName(dto.getLockerName())
                .isAvailable(dto.getIsAvailable())
                .address(dto.getAddress())
                .addressEnglish(dto.getAddressEnglish())
                .addressDetail(dto.getAddressDetail())
                .latitude(dto.getLatitude())
                .longitude(dto.getLongitude())
                .keeper(Member.withId(dto.getKeeperId()))
                .build();

        lockerMapper.insertLocker(locker); // 등록 + lockerId 반환

        List<Long> imageIds = new ArrayList<>();
        if (dto.getImages() != null && !dto.getImages().isEmpty()) {

            if (dto.getImages().size() > 5) {
                throw new ImageException(EXCEED_IMAGE_COUNT);
            }

            for (MultipartFile file : dto.getImages()) {

                if (file.isEmpty()) {
                    throw new ImageException(EMPTY_FILE);
                }

                // 1. 파일명 + 경로 지정
                String uuid = UUID.randomUUID().toString();
                String fileName = uuid + "_" + file.getOriginalFilename();
                String path = "lockers/" + fileName;

                // 2. 업로드 및 예외 처리
                String imageUrl;
                try {
                    imageUrl = s3Utils.upload(file, path); // 확장자 검사 포함됨
                } catch (IOException e) {
                    throw new ImageException(UPLOAD_FAILED); // 필요 시 추가 정의
                }

                // 3. DB 저장
                Image image = Image.builder()
                        .url(imageUrl)
                        .uploadName(file.getOriginalFilename())
                        .build();

                lockerMapper.insertImage(image);
                imageIds.add(image.getImageId());
            }

            lockerMapper.insertLockerImages(locker.getLockerId(), imageIds);
        }

        List<Long> jimTypeIds = dto.getJimTypeIds();
        if (jimTypeIds != null && !jimTypeIds.isEmpty()) {

            // 1. 중복 방지
            Set<Long> uniqueSet = new HashSet<>(jimTypeIds);
            if (uniqueSet.size() != jimTypeIds.size()) {
                throw new LockerException(DUPLICATE_JIMTYPE);
            }

            // 2. 유효성 검사 (DB에 존재하는 ID만 허용)
            List<Long> validJimTypeIds = lockerMapper.findValidJimTypeIds(jimTypeIds);
            if (validJimTypeIds.size() != jimTypeIds.size()) {
                throw new LockerException(INVALID_JIMTYPE);
            }

            // 3. 등록
            lockerMapper.insertLockerJimTypes(locker.getLockerId(), jimTypeIds);
        }

    }

    @Override
    @Transactional
    public void updateLockerActivation(Long lockerId) {
        Locker locker = lockerRepository.findLockerById(lockerId)
                .orElseThrow(()->new LockerException(NOT_FOUND_LOCKER));
        locker.updateIsAvailable(); //더티체킹
    }

    @Override
    public boolean isExistLocker(Long memberId) {
        return lockerMapper.findLockerByMemberId(memberId) > 0;
    }

    @Override
    public LockerUpdateResponse findUpdateUserById(Long lockerId) {
        // 0. 보관소 기본 정보
        LockerUpdateResponse result = lockerMapper.findUpdateLockerById(lockerId);
        if (result == null) {
            throw new LockerException(NOT_FOUND_LOCKERDETAILS);
        }

        result.setImages(lockerMapper.findImageById(lockerId)); // 이미지 리스트 포함

        // 1. 전체 짐 타입 (5개 고정)
        List<LockerJimTypeUpdateResult> allTypes = lockerMapper.findAllJimTypes();

        // 2. 선택된 짐 타입 ID만 따로 조회
        List<Long> selectedIds = lockerMapper.findJimTypeIdsByLocker(lockerId);
        Set<Long> selectedIdSet = new HashSet<>(selectedIds); // 빠른 contains 체크용

        // 3. enabled 플래그 설정
        for (LockerJimTypeUpdateResult type : allTypes) {
            type.setEnabled(selectedIdSet.contains(type.getJimTypeId()));
        }

        // 4. 전체 타입을 반영 (선택 여부 포함)
        result.setJimTypeResults(allTypes);

        return result;
    }

    @Override
    public LockerUpdateResponse findUpdateMyLocker(Long memberId) {
        LockerUpdateResponse result = lockerMapper.findUpdateLockerDetailById(memberId);
        if (result == null) {
            throw new LockerException(NOT_FOUND_LOCKERDETAILS);
        }

        result.setImages(lockerMapper.findImageById(result.getLockerId()));
        return result;
    }

    @Transactional
    @Override
    public void updateLocker(LockerUpdateRequest dto) throws IOException {
        if (!lockerMapper.isExistLocker(dto.getLockerId())) {
            throw new LockerException(NOT_FOUND_LOCKER);
        }

        Locker locker = Locker.builder()
                .lockerId(dto.getLockerId())
                .lockerName(dto.getLockerName())
                .isAvailable(dto.getIsAvailable())
                .address(dto.getAddress())
                .addressEnglish(dto.getAddressEnglish())
                .addressDetail(dto.getAddressDetail())
                .latitude(dto.getLatitude())
                .longitude(dto.getLongitude())
                .keeper(Member.withId(dto.getKeeperId()))
                .build();

        lockerMapper.updateLockerInfo(locker);

        // 이미지 갱신
        if (dto.getImages() != null && !dto.getImages().isEmpty()) {
            lockerMapper.deleteLockerImages(dto.getLockerId());

            List<Long> imageIds = new ArrayList<>();
            for (MultipartFile file : dto.getImages()) {
                if (file.isEmpty()) continue;

                String uuid = UUID.randomUUID().toString();
                String fileName = uuid + "_" + file.getOriginalFilename();
                String path = "lockers/" + fileName;
                String url = s3Utils.upload(file, path);

                Image image = Image.builder()
                        .url(url)
                        .uploadName(file.getOriginalFilename())
                        .build();

                lockerMapper.insertImage(image);
                imageIds.add(image.getImageId());
            }
            lockerMapper.insertLockerImages(dto.getLockerId(), imageIds);
        }

        // 짐 타입 연결 갱신 (기존 제거 후 재등록)
        if (dto.getJimTypeIds() != null && !dto.getJimTypeIds().isEmpty()) {
            lockerMapper.deleteLockerJimTypes(dto.getLockerId());
            lockerMapper.insertLockerJimTypes(dto.getLockerId(), dto.getJimTypeIds());
        }
    }

    @Transactional
    @Override
    public void deleteLocker(Long lockerId) {
        if (!lockerMapper.isExistLocker(lockerId)) {
            throw new LockerException(NOT_FOUND_LOCKER);
        }

        // 1. 연결된 이미지 먼저 삭제 (LockerImage 테이블)
        lockerMapper.deleteLockerImages(lockerId);

        // 2. 연결된 짐타입 삭제 (LockerJimType 테이블)
        lockerMapper.deleteLockerJimTypes(lockerId);

        // 3. 보관소 자체 삭제
        lockerMapper.deleteLocker(lockerId);
    }

}