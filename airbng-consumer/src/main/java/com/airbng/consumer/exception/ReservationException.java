package com.airbng.consumer.exception;

import com.airbng.platform.common.exception.DomainException;
import com.airbng.platform.common.response.status.BaseResponseStatus;
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
