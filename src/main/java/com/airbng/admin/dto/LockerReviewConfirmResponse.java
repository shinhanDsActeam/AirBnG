package com.airbng.admin.dto;

import com.airbng.admin.domain.base.ReviewStatus;
import com.airbng.admin.domain.review.PendingLocker;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LockerReviewConfirmResponse {
    private Long pendingLockerId;
    private ReviewStatus state;
    private Long charge;

    public static LockerReviewConfirmResponse of(PendingLocker pendingLocker, ReviewStatus newState){
        return LockerReviewConfirmResponse.builder()
                .pendingLockerId(pendingLocker.getPendingLockerId())
                .state(newState)
                .build();
    }

}
