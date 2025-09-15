package com.airbng.platform.common.exception;

import com.airbng.platform.common.response.status.BaseResponseStatus;
import lombok.Getter;

@Getter
public class SessionException extends DomainException {

    public SessionException(BaseResponseStatus baseResponseStatus) {
        super(baseResponseStatus);
    }

    public SessionException(BaseResponseStatus baseResponseStatus, String detailMessage) {
        super(baseResponseStatus, detailMessage);
    }
}