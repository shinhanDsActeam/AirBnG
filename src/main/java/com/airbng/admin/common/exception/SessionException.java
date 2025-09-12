package com.airbng.admin.common.exception;

import com.airbng.admin.common.response.status.BaseResponseStatus;
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