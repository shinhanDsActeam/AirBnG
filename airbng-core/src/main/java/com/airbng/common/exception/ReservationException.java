package com.airbng.common.exception;

import com.airbng.common.response.status.BaseResponseStatus;
import com.airbng.common.response.status.ResponseStatus;
import lombok.Getter;

@Getter
public class ReservationException extends DomainException {
    public ReservationException(BaseResponseStatus baseResponseStatus) {
        super(baseResponseStatus);
    }
    public ReservationException(BaseResponseStatus baseResponseStatus, String detailMessage) {
        super(baseResponseStatus, detailMessage);
    }
}
