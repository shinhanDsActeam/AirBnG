package com.airbng.pay.exception;

import com.airbng.platform.common.exception.DomainException;
import com.airbng.platform.common.response.status.BaseResponseStatus;
import lombok.Getter;

@Getter
public class PaymentException extends DomainException {
    public PaymentException(BaseResponseStatus baseResponseStatus) {
        super(baseResponseStatus);
    }
    public PaymentException(BaseResponseStatus baseResponseStatus, String detailMessage) {
        super(baseResponseStatus, detailMessage);
    }
}
