package com.airbng.common.exception;

import com.airbng.common.response.status.ResponseStatus;
import lombok.Getter;

@Getter
public class ReservationException extends RuntimeException {
    private final ResponseStatus exceptionStatus;

    public ReservationException(ResponseStatus exceptionStatus) {
        super(exceptionStatus.getMessage());
        this.exceptionStatus = exceptionStatus;
    }

    public ReservationException(ResponseStatus exceptionStatus, String detailMessage) {
        super(detailMessage);
        this.exceptionStatus = exceptionStatus;
    }
}
