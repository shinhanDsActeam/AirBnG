package com.airbng.consumer.dto.locker;

import com.airbng.consumer.domain.Locker;
import com.airbng.consumer.dto.jimType.JimTypeResult;
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
                .isAvailable(String.valueOf(locker.getIsAvailable()))
                .longitude(locker.getLongitude())
                .latitude(locker.getLatitude())
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