package com.airbng.admin.dto;

import com.airbng.admin.domain.base.ReviewStatus;
import com.airbng.admin.domain.review.PendingLocker;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LockerReviewListResponse {
    private Long lockerReviewId;
    private String lockerName;
    private ReviewStatus reviewStatus;
    private Long memberId;

    public static LockerReviewListResponse from(PendingLocker pendingLocker){
        return LockerReviewListResponse.builder()
                .lockerReviewId(pendingLocker.getPendingLockerId())
                .lockerName(pendingLocker.getLockerName())
                .reviewStatus(pendingLocker.getReviewStatus())
                .memberId(pendingLocker.getMemberId())
                .build();
    }
}
