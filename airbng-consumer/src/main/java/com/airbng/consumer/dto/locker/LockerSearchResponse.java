package com.airbng.consumer.dto.locker;

import com.airbng.consumer.domain.Locker;
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

    public static LockerSearchResponse from(List<Locker> lockers){
        return LockerSearchResponse.builder()
                .count((long) lockers.size())
                .lockers(lockers.stream()
                        .map(LockerPreviewResult::from)
                        .toList())
                .build();
    }
}
