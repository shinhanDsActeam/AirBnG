package com.airbng.common.exception;

import com.airbng.common.response.status.ResponseStatus;
import lombok.Getter;

@Getter
public class LocationException extends RuntimeException {
    private final ResponseStatus exceptionStatus;

    public LocationException(ResponseStatus exceptionStatus) {
        super(exceptionStatus.getMessage());
        this.exceptionStatus = exceptionStatus;
    }

    public LocationException(ResponseStatus exceptionStatus, String detailMessage) {
        super(detailMessage);
        this.exceptionStatus = exceptionStatus;
    }
}
