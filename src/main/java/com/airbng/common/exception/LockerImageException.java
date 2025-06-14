package com.airbng.common.exception;

import com.airbng.common.response.status.ResponseStatus;
import lombok.Getter;

@Getter
public class LockerImageException extends RuntimeException {
    private final ResponseStatus exceptionStatus;

    public LockerImageException(ResponseStatus exceptionStatus) {
        super(exceptionStatus.getMessage());
        this.exceptionStatus = exceptionStatus;
    }

    public LockerImageException(ResponseStatus exceptionStatus, String detailMessage) {
        super(detailMessage);
        this.exceptionStatus = exceptionStatus;
    }
}
