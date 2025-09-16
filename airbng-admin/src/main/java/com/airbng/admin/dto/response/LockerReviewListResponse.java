package com.airbng.admin.dto.response;

import com.airbng.admin.domain.LockerReview;
import com.airbng.admin.domain.PendingLocker;
import com.airbng.admin.domain.base.ReviewStatus;
import com.airbng.api.consumer.dto.view.LockerReviewMemberView;
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
    private String memberName;

    public static LockerReviewListResponse from(LockerReview review, PendingLocker pendingLocker,  LockerReviewMemberView member){
        return LockerReviewListResponse.builder()
                .lockerReviewId(review.getLockerReviewId())
                .lockerName(pendingLocker.getLockerName())
                .reviewStatus(review.getReviewStatus())
                .memberId(pendingLocker.getMemberId())
                .memberName(member.getMemberName())
                .build();
    }

}
