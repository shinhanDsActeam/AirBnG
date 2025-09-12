package com.airbng.admin.common.exception;

import com.airbng.admin.common.response.status.BaseResponseStatus;
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
