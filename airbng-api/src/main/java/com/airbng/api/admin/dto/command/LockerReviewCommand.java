package com.airbng.api.admin.dto.command;

import lombok.AllArgsConstructor;

import java.util.List;

// 보관소 심사 요청 DTO 예시
@AllArgsConstructor
public class LockerReviewCommand {
    private Long memberId;

    private String address;

    private String addressEnglish;

    private String addressDetail;

    private Double latitude;

    private Double longitude;

    private List<Long> jimTypeIds;
}
