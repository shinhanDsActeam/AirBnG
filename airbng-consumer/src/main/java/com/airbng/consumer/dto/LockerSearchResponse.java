package com.airbng.consumer.dto;

import com.airbng.consumer.dto.locker.LockerPreviewResult;
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
