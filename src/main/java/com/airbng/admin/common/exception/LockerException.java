package com.airbng.admin.common.exception;

import com.airbng.admin.common.response.status.BaseResponseStatus;
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
