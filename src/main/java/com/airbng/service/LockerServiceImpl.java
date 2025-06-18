package com.airbng.service;

import com.airbng.common.exception.LockerException;
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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import static com.airbng.common.response.status.BaseResponseStatus.NOT_FOUND_LOCKER;
import static com.airbng.common.response.status.BaseResponseStatus.NOT_FOUND_LOCKERDETAILS;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import static com.airbng.common.response.status.BaseResponseStatus.*;


@Slf4j
@Service
@RequiredArgsConstructor
public class LockerServiceImpl implements LockerService {

    private final LockerMapper lockerMapper;
  
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

    @Transactional
    @Override
    public void registerLocker(LockerInsertRequest dto) {
        log.info("서비스 객체");

        log.info("보관소 존재 여부: {}", lockerMapper.findLockerByMemberId(dto.getKeeperId()));

        if (lockerMapper.findLockerByMemberId(dto.getKeeperId()) > 0) {
            log.info("예외처리");
            throw new LockerException(MEMBER_ALREADY_HAS_LOCKER);
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
            for (ImageInsertRequest imgDTO : dto.getImages()) {
                Image image = Image.builder()
                        .url(imgDTO.getUrl())
                        .uploadName(imgDTO.getUploadName())
                        .build();
                lockerMapper.insertImage(image); // imageId 자동 세팅
                imageIds.add(image.getImageId());
            }

            // 3. LockerImage 테이블에 insert
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

