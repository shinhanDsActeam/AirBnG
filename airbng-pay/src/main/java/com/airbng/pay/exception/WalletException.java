package com.airbng.pay.exception;

import com.airbng.platform.common.exception.DomainException;
import com.airbng.platform.common.response.status.BaseResponseStatus;
import lombok.Getter;

@Getter
public class WalletException extends DomainException {
    public WalletException(BaseResponseStatus status) {
        super(status);
    }

    public WalletException(BaseResponseStatus status, String detailMessage) {
        super(status, detailMessage);
    }
}
