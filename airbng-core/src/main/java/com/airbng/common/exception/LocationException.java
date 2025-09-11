package com.airbng.common.exception;

import com.airbng.common.response.status.BaseResponseStatus;
import com.airbng.common.response.status.ResponseStatus;
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
