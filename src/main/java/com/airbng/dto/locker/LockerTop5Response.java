package com.airbng.dto.locker;

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
