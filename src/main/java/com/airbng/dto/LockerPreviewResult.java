package com.airbng.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LockerPreviewResult {
    private Long lockerId;
    private String address;
    private String lockerName;
    private String isAvailable;
    private String url;
    private List<JimTypeResult> jimTypeResults;
}
