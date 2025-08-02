package com.airbng.dto.jimType;

import com.airbng.domain.jimtype.LockerJimType;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JimTypeResult {
    private Long jimTypeId;
    private String typeName;
    public static JimTypeResult from(LockerJimType lockerJimType){
        return JimTypeResult.builder()
                .jimTypeId(lockerJimType.getJimType().getJimTypeId())
                .typeName(lockerJimType.getJimType().getTypeName())
                .build();
    }
}
