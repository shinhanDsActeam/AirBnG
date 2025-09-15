package com.airbng.consumer.dto.locker;

import com.airbng.consumer.domain.Locker;
import com.airbng.common.base.Available;
import com.airbng.consumer.dto.jimType.LockerJimTypeUpdateResult;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LockerUpdateResponse {

    private Long lockerId;
    private String lockerName;
    private String address;
    private String addressEnglish;
    private String addressDetail;
    private Double latitude;
    private Double longitude;
    private Available isAvailable;
    private Long keeperId;
    private String keeperName;
    private String keeperPhone;
    private List<LockerJimTypeUpdateResult> jimTypeResults;
    private List<String> images; // 이미지 리스트

    public static LockerUpdateResponse from(Locker locker){
        return LockerUpdateResponse.builder()
                .lockerId(locker.getLockerId())
                .lockerName(locker.getLockerName())
                .address(locker.getAddress())
                .addressEnglish(locker.getAddressEnglish())
                .addressDetail(locker.getAddressDetail())
                .latitude(locker.getLatitude())
                .longitude(locker.getLongitude())
                .isAvailable(locker.getIsAvailable())
                .keeperId(locker.getKeeper().getMemberId())
                .keeperName(locker.getKeeper().getName())
                .keeperPhone(locker.getKeeper().getPhone())
                .jimTypeResults(
                        locker.getLockerJimTypes().stream()
                                .map(jt -> LockerJimTypeUpdateResult.of(jt, true))
                                .collect(Collectors.toList())
                )
                .images(
                        locker.getLockerImages().stream()
                                .map(lockerImage -> lockerImage.getImage().getUrl())
                                .collect(Collectors.toList())
                )
                .build();
    }
}