package com.airbng.admin.service;

import com.airbng.admin.domain.LockerReview;
import com.airbng.admin.domain.PendingLockerImage;
import com.airbng.admin.domain.PendingLockerJimtype;
import com.airbng.admin.domain.base.ReviewStatus;
import com.airbng.admin.repository.LockerReviewRepository;
import com.airbng.admin.repository.PendingLockerRepository;
import com.airbng.api.consumer.LockerApi;
import com.airbng.api.consumer.dto.command.LockerReviewApproveCommand;
import com.airbng.api.consumer.dto.command.LockerReviewRejectCommand;
import com.airbng.platform.common.exception.DomainException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.airbng.platform.common.response.status.BaseResponseStatus.*;

@Log4j2
@Service
@RequiredArgsConstructor
public class LockerReviewServiceImpl implements LockerReviewService{

    private final LockerReviewRepository lockerReviewRepository;
    private final LockerApi lockerApi; // 유저 내부 모듈 API

    @Transactional
    public boolean approveLockerReview(Long pendingLockerId, Long memberId) {

        LockerReview lockerReview = lockerReviewRepository.findById(pendingLockerId)
                .orElseThrow(() -> new DomainException(NOT_FOUND_LOCKERDETAILS));

        // 상태 업데이트
        lockerReview.updateState(ReviewStatus.APPROVED);

//        lockerReviewRepository.save(review);

        // PendingLocker -> DTO 변환
        LockerReviewApproveCommand command = LockerReviewApproveCommand.builder()
                .memberId(memberId)
                .lockerName(lockerReview.getPendingLocker().getLockerName())
                .address(lockerReview.getPendingLocker().getAddress())
                .addressEnglish(lockerReview.getPendingLocker().getAddressEnglish())
                .addressDetail(lockerReview.getPendingLocker().getAddressDetail())
                .latitude(lockerReview.getPendingLocker().getLatitude())
                .longitude(lockerReview.getPendingLocker().getLongitude())
                .lockerType(lockerReview.getPendingLocker().getLockerType().toString())
                .jimTypeId(lockerReview.getPendingLocker().getPendingLockerJimtypes().stream()
                        .map(PendingLockerJimtype::getJimtypeId)
                        .toList())
                .imageId(lockerReview.getPendingLocker().getPendingLockerImages().stream()
                        .map(PendingLockerImage::getImageId)
                        .toList())
                .status(lockerReview.getPendingLocker().getStatus())
                .build();

        // 유저 서버에 전달
        return lockerApi.createLockerFromPending(command);
    }

    @Transactional
    public boolean rejectLockerReview(Long pendingLockerId, Long memberId, String reason) {

        LockerReview lockerReview = lockerReviewRepository.findById(pendingLockerId)
                .orElseThrow(() -> new DomainException(NOT_FOUND_LOCKERDETAILS));

        // 상태 업데이트
        lockerReview.updateState(ReviewStatus.REJECTED);
        lockerReview.updateComment(reason);

//        lockerReviewRepository.save(review);

        // PendingLocker -> DTO 변환
        LockerReviewRejectCommand command = LockerReviewRejectCommand.builder()
                .memberId(memberId)
                .lockerName(lockerReview.getPendingLocker().getLockerName())
                .reason(lockerReview.getReviewComment())
                .build();

        // 유저 서버에 전달
        return lockerApi.rejectLockerReview(command);
    }


}
