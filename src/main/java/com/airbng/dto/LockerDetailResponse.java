package com.airbng.dto;

import lombok.*;

import java.util.List;
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LockerDetailResponse {

    private Long lockerId;
    private String lockerName;
    private String address;
    private String addressEnglish;
    private String addressDetail;
    private Long keeperId;
    private String keeperName;
    private List<String> images; // 이미지 리스트

}
