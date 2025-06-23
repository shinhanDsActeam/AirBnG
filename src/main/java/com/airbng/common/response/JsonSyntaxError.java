package com.airbng.common.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
public class JsonSyntaxError {
    private int lineNumber;
    private String rejectValue;
    private String message;
}