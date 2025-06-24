package com.airbng.common.response;

import com.airbng.common.response.status.ResponseStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@JsonPropertyOrder({"code", "message", "timestamp", "result"})
public class BaseErrorResponse<T> implements ResponseStatus {

    private final int code;
    @JsonIgnore
    private int httpStatus;
    private final String message;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime timestamp;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final T result;

    public BaseErrorResponse(ResponseStatus status) {
        this.code = status.getCode();
        this.message = status.getMessage();
        this.timestamp = LocalDateTime.now();
        this.result = null;
    }

    public BaseErrorResponse(ResponseStatus status, String message) {
        this.code = status.getCode();
        this.message = message;
        this.timestamp = LocalDateTime.now();
        this.result = null;
    }

    public BaseErrorResponse(ResponseStatus status, T result) {
        this.code = status.getCode();
        this.message = status.getMessage();
        this.timestamp = LocalDateTime.now();
        this.result = result;
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public int getHttpStatus() {
        return httpStatus;
    }

    @Override
    public String getMessage() {
        return message;
    }

}

