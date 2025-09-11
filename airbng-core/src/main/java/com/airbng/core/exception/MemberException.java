package com.airbng.core.exception;

import com.airbng.platform.common.exception.DomainException;
import com.airbng.platform.common.response.status.BaseResponseStatus;
import lombok.Getter;

@Getter
public class MemberException extends DomainException {
    public MemberException(BaseResponseStatus baseResponseStatus) {
        super(baseResponseStatus);
    }
    public MemberException(BaseResponseStatus baseResponseStatus, String detailMessage) {
        super(baseResponseStatus, detailMessage);
    }
}
