package com.airbng.common.exception;

import com.airbng.common.response.status.BaseResponseStatus;
import lombok.Getter;

@Getter
public class ZzimException extends DomainException {

    public ZzimException(BaseResponseStatus baseResponseStatus) {
        super(baseResponseStatus);
    }

    public ZzimException(BaseResponseStatus baseResponseStatus, String detailMessage) {
        super(baseResponseStatus, detailMessage);
    }
}