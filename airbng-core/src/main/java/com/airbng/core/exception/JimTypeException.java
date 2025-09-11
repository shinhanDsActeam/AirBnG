package com.airbng.core.exception;

import com.airbng.platform.common.exception.DomainException;
import com.airbng.platform.common.response.status.BaseResponseStatus;
import lombok.Getter;

@Getter
public class JimTypeException extends DomainException {
    public JimTypeException(BaseResponseStatus baseResponseStatus) {
        super(baseResponseStatus);
    }
    public JimTypeException(BaseResponseStatus baseResponseStatus, String detailMessage) {
        super(baseResponseStatus, detailMessage);
    }
}
