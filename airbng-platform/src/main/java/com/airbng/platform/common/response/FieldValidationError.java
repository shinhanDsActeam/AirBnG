package com.airbng.platform.common.response;

import lombok.*;

@AllArgsConstructor
@Getter
@Builder
public class FieldValidationError {
    private String fieldName;
    private String rejectValue;
    private String message;
}