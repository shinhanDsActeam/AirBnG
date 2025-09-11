package com.airbng.common.response;


import com.airbng.common.response.status.ResponseStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;

import static com.airbng.common.response.status.BaseResponseStatus.SUCCESS;

@Getter
@JsonPropertyOrder({"code", "message", "result"})
public class BaseResponse<T> implements ResponseStatus {

    private final int code;
    @JsonIgnore
    private int httpStatus;
    private final String message;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final T result;

    public BaseResponse(ResponseStatus status) {
        this.code = status.getCode();
        this.message = status.getMessage();
        this.result = null;
    }

    public BaseResponse(T result) {
        this.code = SUCCESS.getCode();
        this.message = SUCCESS.getMessage();
        this.result = result;
    }
    public BaseResponse(ResponseStatus status, T result) {
        this.code = status.getCode();
        this.message = status.getMessage();
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
