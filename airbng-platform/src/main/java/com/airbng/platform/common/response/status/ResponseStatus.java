package com.airbng.platform.common.response.status;

public interface ResponseStatus {

    int getCode();

    int getHttpStatus();

    String getMessage();

}