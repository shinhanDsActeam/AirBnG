package com.airbng.dto.jimType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JimTypeCountResult {
    public Long jimTypeId; // 맡길 짐 타입 ID
    public Long count; // 몇개
}