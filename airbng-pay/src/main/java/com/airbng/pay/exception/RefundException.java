package com.airbng.pay.exception;

import com.airbng.platform.common.exception.DomainException;
import com.airbng.platform.common.response.status.BaseResponseStatus;
import lombok.Getter;

@Getter
public class RefundException extends DomainException {
    public RefundException(BaseResponseStatus status) {
        super(status);
    }

    public RefundException(BaseResponseStatus status, String detailMessage) {
        super(status, detailMessage);
    }
}
