package com.airbng.consumer.dto.jimType;

import com.airbng.consumer.domain.jimtype.LockerJimType;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LockerJimTypeResult { // 보관소가 관리하는 짐 타입
    private Long jimTypeId;
    private String typeName;
    private Long pricePerHour;

    public static LockerJimTypeResult from(LockerJimType lockerJimType){
        return LockerJimTypeResult.builder()
                .jimTypeId(lockerJimType.getJimType().getJimTypeId())
                .typeName(lockerJimType.getJimType().getTypeName())
                .pricePerHour(lockerJimType.getJimType().getPricePerHour())
                .build();
    }

}