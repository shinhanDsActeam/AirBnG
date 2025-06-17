package com.airbng.common.response.status;

public interface ResponseStatus {

    int getCode();

    int getHttpStatus();

    String getMessage();

}