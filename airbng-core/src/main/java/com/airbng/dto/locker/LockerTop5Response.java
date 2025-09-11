package com.airbng.dto.locker;

import com.airbng.domain.Locker;
import lombok.*;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LockerTop5Response {
    private List<LockerPreviewResult> lockers;

    public static LockerTop5Response from(List<Locker> lockers){
        return LockerTop5Response.builder()
                .lockers(lockers.stream()
                        .map(LockerPreviewResult::from)
                        .collect(Collectors.toList()))
                .build();
    }
}
