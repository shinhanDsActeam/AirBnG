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
public class JimTypeCountResult {
    @NotNull @Min(1)
    public Long jimTypeId; // 맡길 짐 타입 ID
    @NotNull @Min(1)
    public Long count; // 몇개
}