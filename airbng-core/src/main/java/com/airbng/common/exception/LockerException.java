package com.airbng.common.exception;

import com.airbng.common.response.status.BaseResponseStatus;
import com.airbng.common.response.status.ResponseStatus;
import lombok.Getter;

@Getter
public class LockerException extends DomainException {
    public LockerException(BaseResponseStatus baseResponseStatus) {
        super(baseResponseStatus);
    }
    public LockerException(BaseResponseStatus baseResponseStatus, String detailMessage) {
        super(baseResponseStatus, detailMessage);
    }
}
