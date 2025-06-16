package com.airbng.common.exception;

import com.airbng.common.response.status.ResponseStatus;
import lombok.Getter;

@Getter
public class MemberException extends RuntimeException {
    private final ResponseStatus exceptionStatus;

    public MemberException(ResponseStatus exceptionStatus) {
        super(exceptionStatus.getMessage());
        this.exceptionStatus = exceptionStatus;
    }

    public MemberException(ResponseStatus exceptionStatus, String detailMessage) {
        super(detailMessage);
        this.exceptionStatus = exceptionStatus;
    }
}
