package com.airbng.core.exception;

import com.airbng.platform.common.exception.DomainException;
import com.airbng.platform.common.response.status.BaseResponseStatus;
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
