package com.airbng.core.exception;

import com.airbng.platform.common.exception.DomainException;
import com.airbng.platform.common.response.status.BaseResponseStatus;
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
