package com.airbng.admin.dto.response;

import com.airbng.admin.domain.LockerReview;
import com.airbng.admin.domain.PendingLocker;
import com.airbng.admin.domain.base.LockerType;
import com.airbng.admin.domain.base.ReviewStatus;
import com.airbng.api.consumer.dto.view.LockerJimTypeResult;
import com.airbng.api.consumer.dto.view.LockerReviewDetailView;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LockerReviewDetailResponse {


    private Long lockerId;
    private String lockerName;
    private String address;
    private String addressEnglish;
    private String addressDetail;
    private Long memberId;
    private String memberName;
    private String memberPhone;
    private LockerType lockerType; // 보관함 타입

    private ReviewStatus reviewStatus;
    private String reviewComment;

    private List<LockerJimTypeResult> jimTypeResults; // 짐 타입 목록; // 종류
    private List<String> images; // 이미지 리스트

    public static LockerReviewDetailResponse from(PendingLocker pendingLocker, LockerReview lockerReview, LockerReviewDetailView userData ){
        return LockerReviewDetailResponse.builder()
                .lockerId(pendingLocker.getPendingLockerId())
                .lockerName(pendingLocker.getLockerName())
                .address(pendingLocker.getAddress())
                .addressEnglish(pendingLocker.getAddressEnglish())
                .addressDetail(pendingLocker.getAddressDetail())
                .memberId(pendingLocker.getMemberId())
                .memberName(userData.getMemberName())
                .memberPhone(userData.getMemberPhone())
                .lockerType(pendingLocker.getLockerType())
                .reviewStatus(lockerReview != null
                        ? lockerReview.getReviewStatus()
                        : null)
                .reviewComment(lockerReview!= null
                        && lockerReview.getReviewStatus() == ReviewStatus.REJECTED
                        ? lockerReview.getReviewComment()
                        : null)
                .jimTypeResults(userData.getJimTypes())
                .images(userData.getImageUrls())
                .build();
    }


}
