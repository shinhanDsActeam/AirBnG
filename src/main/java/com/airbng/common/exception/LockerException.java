package com.airbng.common.exception;

import com.airbng.common.response.status.ResponseStatus;
import lombok.Getter;

@Getter
public class LockerException extends RuntimeException {
    private final ResponseStatus exceptionStatus;

    public LockerException(ResponseStatus exceptionStatus) {
        super(exceptionStatus.getMessage());
        this.exceptionStatus = exceptionStatus;
    }

    public LockerException(ResponseStatus exceptionStatus, String detailMessage) {
        super(detailMessage);
        this.exceptionStatus = exceptionStatus;
    }
}
