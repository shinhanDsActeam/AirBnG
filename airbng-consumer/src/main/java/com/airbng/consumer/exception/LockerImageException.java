package com.airbng.consumer.exception;

import com.airbng.platform.common.exception.DomainException;
import com.airbng.platform.common.response.status.BaseResponseStatus;
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
