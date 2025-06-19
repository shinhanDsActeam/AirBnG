package com.airbng.service;

import com.airbng.common.exception.LockerException;
import com.airbng.dto.JimTypeResult;
import com.airbng.dto.LockerPreviewResult;
import com.airbng.dto.LockerSearchRequest;
import com.airbng.dto.LockerSearchResponse;
import com.airbng.mappers.LockerMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;
import com.airbng.common.exception.ImageException;
import com.airbng.common.exception.LockerException;
import com.airbng.common.exception.MemberException;
import com.airbng.dto.LockerDetailResponse;
import com.airbng.common.response.status.BaseResponseStatus;
import com.airbng.domain.Locker;
import com.airbng.domain.Member;
import com.airbng.domain.base.Available;
import com.airbng.domain.base.ReservationState;
import com.airbng.domain.image.Image;
import com.airbng.dto.ImageInsertRequest;
import com.airbng.dto.LockerInsertRequest;
import com.airbng.dto.LockerPreviewResult;
import com.airbng.dto.LockerTop5Response;
import com.airbng.mappers.LockerMapper;
import com.airbng.util.S3Uploader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import static com.airbng.common.response.status.BaseResponseStatus.NOT_FOUND_LOCKER;
import static com.airbng.common.response.status.BaseResponseStatus.NOT_FOUND_LOCKERDETAILS;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

import static com.airbng.common.response.status.BaseResponseStatus.*;


@Slf4j
@Service
@RequiredArgsConstructor
public class LockerServiceImpl implements LockerService {

    private final LockerMapper lockerMapper;
    private final S3Uploader s3Uploader;
  
  
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
    public LockerDetailResponse findUserById(Long lockerId) {
        LockerDetailResponse result = lockerMapper.findUserById(lockerId);
        // 만약 result가 null이라면, 해당 lockerId에 대한 정보가 없다는 예외를 발생시킴
        if (result == null) {
            throw new LockerException(NOT_FOUND_LOCKERDETAILS);
        }
        result.setImages(lockerMapper.findImageById(lockerId));
        return result;

    }

    @Override
    public LockerTop5Response findTop5Locker(){
        List<LockerPreviewResult> popularLockers = lockerMapper.findTop5Lockers(ReservationState.CONFIRMED);

        if(popularLockers.isEmpty()) throw new LockerException(NOT_FOUND_LOCKER);

        return LockerTop5Response.builder()
                .lockers(popularLockers)
                .build();
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
            throw new MemberException(MEMBER_NOT_FOUND);
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
                throw new ImageException(BaseResponseStatus.EXCEED_IMAGE_COUNT);
            }

            for (MultipartFile file : dto.getImages()) {

                if (file.isEmpty()) {
                    throw new ImageException(BaseResponseStatus.EMPTY_FILE);
                }

                // 1. 파일명 + 경로 지정
                String uuid = UUID.randomUUID().toString();
                String fileName = uuid + "_" + file.getOriginalFilename();
                String path = "lockers/" + fileName;

                // 2. 업로드 및 예외 처리
                String imageUrl;
                try {
                    imageUrl = s3Uploader.upload(file, path); // 확장자 검사 포함됨
                } catch (IOException e) {
                    throw new ImageException(BaseResponseStatus.UPLOAD_FAILED); // 필요 시 추가 정의
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
}