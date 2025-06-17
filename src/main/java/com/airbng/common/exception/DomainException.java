package com.airbng.common.exception;

import com.airbng.common.response.status.BaseResponseStatus;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Slf4j
public class DomainException extends RuntimeException{
    private final BaseResponseStatus baseResponseStatus;

    public DomainException(BaseResponseStatus status) {
        super(status.getMessage());
        this.baseResponseStatus = status;
        log.info("{} - message : {}", this.getClass().getSimpleName(), status.getMessage());
    }

    public DomainException(BaseResponseStatus status, String detailMessage) {
        super(status.getMessage());
        this.baseResponseStatus = status;
        log.info("{} - message : {}", this.getClass().getSimpleName(), detailMessage);
    }


}