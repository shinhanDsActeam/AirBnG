package com.airbng.dto;

import com.airbng.dto.locker.LockerPreviewResult;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LockerSearchResponse {
    private Long count;
    private List<LockerPreviewResult> lockers;
}
