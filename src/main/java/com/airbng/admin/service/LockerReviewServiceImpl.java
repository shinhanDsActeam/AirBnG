package com.airbng.admin.service;

import com.airbng.admin.common.exception.LockerException;
import com.airbng.admin.domain.review.LockerReview;
import com.airbng.admin.domain.review.PendingLocker;
import com.airbng.admin.dto.LockerReviewDetailResponse;
import com.airbng.admin.repository.LockerReviewRepository;

import com.airbng.dto.locker.LockerDetailResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.airbng.admin.common.response.status.BaseResponseStatus.NOT_FOUND_LOCKERDETAILS;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LockerReviewServiceImpl implements LockerReviewService{

    private final LockerReviewRepository lockerReviewRepository;

    // ================= 상세 =================
    @Override
    public LockerReviewDetailResponse findLockerReviewById(Long lockerReviewId) {

        PendingLocker pendingLocker = lockerReviewRepository.findLockerReviewById(lockerReviewId)
                .orElseThrow(() -> new LockerException(NOT_FOUND_LOCKERDETAILS));
        return LockerReviewDetailResponse.from(pendingLocker);

    }
}
