package com.airbng.core.dto.jimType;

import com.airbng.core.domain.jimtype.LockerJimType;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LockerJimTypeUpdateResult {
    private Long jimTypeId;
    private String typeName;
    private Long pricePerHour;
    private boolean enabled; // 선택된 경우 true

    public static LockerJimTypeUpdateResult of(LockerJimType lockerJimType, boolean enabled) {
        if (lockerJimType == null) {
            throw new IllegalArgumentException("짐 타입 정보가 존재하지 않습니다.");
        }

        return LockerJimTypeUpdateResult.builder()
                .jimTypeId(lockerJimType.getJimType().getJimTypeId())
                .typeName(lockerJimType.getJimType().getTypeName())
                .pricePerHour(lockerJimType.getJimType().getPricePerHour())
                .enabled(enabled)
                .build();
    }

}
