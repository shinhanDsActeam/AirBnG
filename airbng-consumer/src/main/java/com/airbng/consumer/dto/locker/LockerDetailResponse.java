package com.airbng.consumer.dto.locker;

import com.airbng.consumer.domain.Locker;
import com.airbng.common.base.Available;
import com.airbng.consumer.dto.jimType.LockerJimTypeResult;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LockerDetailResponse {

    private Long lockerId;
    private String lockerName;
    private String address;
    private String addressEnglish;
    private String addressDetail;
    private Available isAvailable;
    private Long keeperId;
    private String keeperName;
    private String keeperPhone;
    private List<LockerJimTypeResult> jimTypeResults; // 짐 타입 목록; // 종류
    private List<String> images; // 이미지 리스트

    public static LockerDetailResponse from(Locker locker){
        return LockerDetailResponse.builder()
                .lockerId(locker.getLockerId())
                .lockerName(locker.getLockerName())
                .address(locker.getAddress())
                .addressDetail(locker.getAddressDetail())
                .addressEnglish(locker.getAddressEnglish())
                .isAvailable(locker.getIsAvailable())
                .keeperId(locker.getKeeper().getMemberId())
                .keeperName(locker.getLockerName())
                .keeperPhone(locker.getKeeper().getPhone())
                .jimTypeResults(
                        locker.getLockerJimTypes().stream()
                                        .map(LockerJimTypeResult::from)
                                                .collect(Collectors.toList())
                )
                .images(locker.getLockerImages().stream()
                        .map(lockerImage -> lockerImage.getImage().getUrl())
                        .collect(Collectors.toList()))
                .build();


    }

}
