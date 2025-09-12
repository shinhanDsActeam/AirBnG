package com.airbng.admin.common.exception;

import com.airbng.admin.common.response.status.BaseResponseStatus;
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
