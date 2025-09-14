package com.airbng.admin.service;

import com.airbng.admin.api.PendingLockerInsertRequest;
import com.airbng.admin.domain.base.ReviewStatus;
import com.airbng.admin.domain.review.PendingLocker;
import com.airbng.admin.dto.LockerReviewConfirmResponse;
import com.airbng.admin.dto.LockerReviewDetailResponse;
import com.airbng.admin.dto.LockerReviewListResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface LockerReviewService {

    //저장
    void insertPendingLocker(PendingLockerInsertRequest dto);

    //보관소 심사 상세보기
    LockerReviewDetailResponse findLockerReviewById(Long lockerReviewId);

//    LockerReviewDetailResponse findPendingLockerReviewById(Long lockerReviewId);
//
//    LockerReviewDetailResponse findApprovedLockerReviewById(Long lockerReviewId);
//
//    LockerReviewDetailResponse findRejectedLockerReviewById(Long lockerReviewId);

    //보관소 승인/거절
//    void approve(Long pendingLockerId);
//    void reject(Long pendingLockerId, String reason);

    // ================= 목록 + 페이징 =================
    Page<LockerReviewListResponse> findAllByStatus(ReviewStatus status, int page);
}
