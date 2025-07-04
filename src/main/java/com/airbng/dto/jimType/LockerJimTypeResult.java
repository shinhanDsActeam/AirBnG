package com.airbng.dto.jimType;

import com.airbng.domain.jimtype.LockerJimType;
import lombok.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

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