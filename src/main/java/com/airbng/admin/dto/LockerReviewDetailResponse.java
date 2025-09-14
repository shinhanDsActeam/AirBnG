package com.airbng.admin.dto;

import com.airbng.admin.domain.base.LockerType;
import com.airbng.admin.domain.base.ReviewStatus;
import com.airbng.admin.domain.review.PendingLocker;
import com.airbng.admin.domain.review.PendingLockerJimtype;
import com.airbng.dto.jimType.LockerJimTypeResult;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LockerReviewDetailResponse {

    private Long lockerReviewId;
    private String lockerName;
    private String address;
    private String addressEnglish;
    private String addressDetail;

    private Long memberId;
    private String keeperName;
    private String keeperEmail;

    private LockerType lockerType;
    private ReviewStatus reviewStatus;
    private String reviewComment;


    private List<LockerJimTypeResult> jimTypeResults; // 짐 타입 목록; // 종류
    private List<String> images; // 이미지 리스트

    public static LockerReviewDetailResponse from(PendingLocker pendingLocker, Member keeper){
        return LockerReviewDetailResponse.builder()
                .lockerReviewId(pendingLocker.getPendingLockerId())
                .lockerName(pendingLocker.getLockerName())
                .address(pendingLocker.getAddress())
                .addressEnglish(pendingLocker.getAddressEnglish())
                .addressDetail(pendingLocker.getAddressDetail())
                .memberId(pendingLocker.getMemberId())
                .keeperName(keeper.getName())
                .keeperEmail(keeper.getEmail())
                .reviewStatus(pendingLocker.getReviewComment() != null
                        ? pendingLocker.getReviewComment().getReviewStatus()
                        : null)
                .reviewComment(pendingLocker.getReviewComment() != null
                        && pendingLocker.getReviewComment().getReviewStatus() == ReviewStatus.REJECTED
                        ? pendingLocker.getReviewComment().getReviewComment()
                        : null)
                .jimTypeResults(pendingLocker.getPendingLockerJimtypes().stream()
                        .map(plj -> LockerJimTypeResult.from(plj.getJimType()))
                        .toList())
                .images(pendingLocker.getPendingLockerImages().stream()
                        .map(img -> img.getImage().getUrl())
                        .toList())
                .build();
    }

}
