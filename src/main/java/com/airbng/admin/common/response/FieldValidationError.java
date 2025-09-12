package com.airbng.admin.common.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
public class FieldValidationError {
    private String fieldName;
    private String rejectValue;
    private String message;
}