package com.airbng.dto.locker;

import com.airbng.domain.Locker;
import com.airbng.dto.jimType.JimTypeResult;
import com.airbng.dto.jimType.LockerJimTypeResult;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

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
    private Double latitude;
    private Double longitude;
    private List<JimTypeResult> jimTypeResults;

    public static LockerPreviewResult from(Locker locker) {
        return LockerPreviewResult.builder()
                .lockerId(locker.getLockerId())
                .lockerName(locker.getLockerName())
                .address(locker.getAddress())
                .url(locker.getLockerImages().stream()
                        .findFirst()
                        .map(lockerImage -> lockerImage.getImage().getUrl())
                        .orElse(null))
                .jimTypeResults(
                        locker.getLockerJimTypes().stream()
                                .map(JimTypeResult::from)
                                .collect(Collectors.toList())
                )
                .build();
    }
}