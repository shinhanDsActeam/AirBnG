package com.airbng.common.exception;

import com.airbng.common.response.status.BaseResponseStatus;
import com.airbng.common.response.status.ResponseStatus;
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
