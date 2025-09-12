package com.airbng.admin.common.exception;

import com.airbng.admin.common.response.status.BaseResponseStatus;
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
