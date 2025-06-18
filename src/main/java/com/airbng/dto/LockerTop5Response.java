package com.airbng.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LockerTop5Response {
    private List<LockerPreviewResult> lockers;
}
