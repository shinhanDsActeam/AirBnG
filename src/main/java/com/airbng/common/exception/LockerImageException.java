package com.airbng.common.exception;

import com.airbng.common.response.status.BaseResponseStatus;
import com.airbng.common.response.status.ResponseStatus;
import lombok.Getter;

@Getter
public class LockerImageException extends DomainException {
    public LockerImageException(BaseResponseStatus baseResponseStatus) {
        super(baseResponseStatus);
    }
    public LockerImageException(BaseResponseStatus baseResponseStatus, String detailMessage) {
        super(baseResponseStatus, detailMessage);
    }
}
