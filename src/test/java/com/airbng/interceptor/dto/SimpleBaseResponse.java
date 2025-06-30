package com.airbng.interceptor.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SimpleBaseResponse {
    private int code;
    private String message;
}
