package com.airbng.core.dto;

import com.airbng.core.dto.locker.LockerPreviewResult;
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
