package com.airbng.admin.service;

import com.airbng.api.consumer.dto.command.LockerReviewRejectCommand;

public interface LockerReviewService {

    //승인 / 반려
    boolean approveLockerReview(Long pendingLockerId, Long memberId);
    LockerReviewRejectCommand rejectLockerReview(Long pendingLockerId, String reason);
}
