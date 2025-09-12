package com.airbng.admin.dto;

import com.airbng.admin.domain.base.LockerType;
import com.airbng.admin.domain.base.ReviewStatus;
import com.airbng.admin.domain.review.PendingLocker;
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

    private Long lockerId;
    private String lockerName;
    private String address;
    private String addressEnglish;
    private String addressDetail;
    private Long memberId;
    private LockerType lockerType;
    private ReviewStatus reviewStatus;
    //TODO
    private List<LockerJimTypeResult> jimTypeResults; // 짐 타입 목록; // 종류
    private List<String> images; // 이미지 리스트

    public static LockerReviewDetailResponse from(PendingLocker pendingLocker){
        return LockerReviewDetailResponse.builder()
                .lockerId(pendingLocker.getPendingLockerId())
                .lockerName(pendingLocker.getLockerName())
                .address(pendingLocker.getAddress())
                .addressDetail(pendingLocker.getAddressDetail())
                .addressEnglish(pendingLocker.getAddressEnglish())
                .memberId(pendingLocker.getMemberId())
                .jimTypeResults(
                        pendingLocker.getPendingLockerJimtypes().stream()
                                .map(LockerJimTypeResult::from)
                                .collect(Collectors.toList())
                )
                .images(pendingLocker.getPendingLockerImages().stream()
                        .map(lockerImage -> lockerImage.getImage().getUrl())
                        .collect(Collectors.toList()))
                .build();


    }
}
