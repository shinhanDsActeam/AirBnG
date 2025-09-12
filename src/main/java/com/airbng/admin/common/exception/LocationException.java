package com.airbng.admin.common.exception;

import com.airbng.admin.common.response.status.BaseResponseStatus;
import lombok.Getter;

@Getter
public class LocationException extends DomainException {
    public LocationException(BaseResponseStatus baseResponseStatus) {
        super(baseResponseStatus);
    }
    public LocationException(BaseResponseStatus baseResponseStatus, String detailMessage) {
        super(baseResponseStatus, detailMessage);
    }
}
