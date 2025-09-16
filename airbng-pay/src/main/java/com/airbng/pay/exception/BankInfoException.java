package com.airbng.pay.exception;

import com.airbng.platform.common.exception.DomainException;
import com.airbng.platform.common.response.status.BaseResponseStatus;
import lombok.Getter;

@Getter
public class BankInfoException extends DomainException {
    public BankInfoException(BaseResponseStatus status) {
        super(status);
    }

    public BankInfoException(BaseResponseStatus status, String detailMessage) {
        super(status, detailMessage);
    }
}
