package com.airbng.api.consumer.dto.view;
import lombok.*;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LockerJimTypeResult {

    private Long jimTypeId;
    private String typeName;
    private Long pricePerHour;

}
