package com.airbng.admin.service;

import com.airbng.admin.domain.review.PendingLocker;
import com.airbng.admin.dto.LockerReviewConfirmResponse;
import com.airbng.admin.dto.LockerReviewDetailResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface LockerReviewService {

    //보관소 심사 상세보기
    LockerReviewDetailResponse findLockerReviewById(Long lockerReviewId);

    //보관소 승인/거절
    LockerReviewConfirmResponse confirmLockerReviewState(Long reservationId, String approve, Long memberId);

    // ================= 목록 + 페이징 =================
//    Page<PendingLocker> findAll(Pageable page);

    // ================= 목록 + 페이징 =================
    Page<PendingLocker> findAll(int page);
}
