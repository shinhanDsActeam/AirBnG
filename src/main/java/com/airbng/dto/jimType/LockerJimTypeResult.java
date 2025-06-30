package com.airbng.dto.jimType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LockerJimTypeResult { // 보관소가 관리하는 짐 타입
    private Long jimTypeId;
    private String typeName;
    private Long pricePerHour;
}