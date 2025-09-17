package com.airbng.admin.dto.response;

import com.airbng.admin.domain.LockerReview;
import com.airbng.admin.domain.PendingLocker;
import com.airbng.admin.domain.base.ReviewStatus;
import com.airbng.api.consumer.dto.view.LockerReviewMemberView;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LockerReviewListResponse {

    private Long lockerReviewId;
    private String lockerName;
    private String address;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

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
                .address(pendingLocker.getAddress())
                .createdAt(review.getCreatedAt())
                .build();
    }

}
