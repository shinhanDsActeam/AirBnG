package com.airbng.dto.jimType;

import com.airbng.domain.jimtype.JimType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class JimTypeResponse {

    private Long jimTypeId;
    private String typeName;
    private String description;
    private Long pricePerHour;

    public static JimTypeResponse from(JimType e) {
        return JimTypeResponse.builder()
                .jimTypeId(e.getJimTypeId())
                .typeName(e.getTypeName())
                .description(e.getDescription())
                .pricePerHour(e.getPricePerHour())
                .build();
    }

}
