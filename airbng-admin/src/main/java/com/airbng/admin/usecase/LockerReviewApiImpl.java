package com.airbng.admin.usecase;

import com.airbng.admin.domain.LockerReview;
import com.airbng.admin.domain.PendingLocker;
import com.airbng.admin.domain.PendingLockerImage;
import com.airbng.admin.domain.PendingLockerJimtype;
import com.airbng.admin.domain.base.LockerType;
import com.airbng.admin.domain.base.ReviewStatus;
import com.airbng.admin.repository.LockerReviewImageRepository;
import com.airbng.admin.repository.LockerReviewJimTypeRepository;
import com.airbng.admin.repository.LockerReviewRepository;
import com.airbng.api.admin.LockerReviewApi;
import com.airbng.api.admin.dto.command.LockerReviewCommand;
import com.airbng.common.base.BaseStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.awt.*;
import java.time.LocalDateTime;
import java.util.HashSet;

@Service
@RequiredArgsConstructor
public class LockerReviewApiImpl implements LockerReviewApi {

    private final LockerReviewImageRepository lockerReviewImageRepository;
    private final LockerReviewJimTypeRepository lockerreviewJimTypeRepository;
    private final LockerReviewRepository lockerReviewRepository;


    @Override
    public boolean submitLockerForReview(LockerReviewCommand dto) {
        // 1) PendingLocker 생성
        PendingLocker pendingLocker = PendingLocker.builder()
                .lockerName(dto.getLockerName())
                .address(dto.getAddress())
                .addressEnglish(dto.getAddressEnglish())
                .addressDetail(dto.getAddressDetail())
                .latitude(dto.getLatitude())
                .longitude(dto.getLongitude())
                .memberId(dto.getMemberId())
                .lockerType(LockerType.valueOf(dto.getLockerType()))
                .status(BaseStatus.ACTIVE)
                .build();

        // 2) 초기 심사 상태 (WAITING) 세팅
        LockerReview review = LockerReview.builder()
                .reviewStatus(ReviewStatus.WAITING)
                .status(BaseStatus.ACTIVE)
                .pendingLocker(pendingLocker)
                .build();

        pendingLocker.setLockerReview(review);

        lockerReviewRepository.save(pendingLocker);


        // 3) 이미지 연결
        if (dto.getImageId() != null && !dto.getImageId().isEmpty()) {
            for (Long imageId : dto.getImageId()) {
                PendingLockerImage pli = PendingLockerImage.builder()
                        .pendingLocker(pendingLocker)
                        .imageId(imageId)
                        .status(BaseStatus.ACTIVE)
                        .build();

                lockerReviewImageRepository.save(pli);
            }
        }

        // 5) JimType 연결 (유저가 보낸 리스트 기반)
        if (dto.getJimTypeId() != null && !dto.getJimTypeId().isEmpty()) {
            for (Long jimTypeId : dto.getJimTypeId()) {
                lockerreviewJimTypeRepository.save(
                        PendingLockerJimtype.builder()
                                .pendingLocker(pendingLocker)
                                .jimtypeId(jimTypeId)
                                .status(BaseStatus.ACTIVE)
                                .build()
                );
            }
        }

        return true;
    }
}
