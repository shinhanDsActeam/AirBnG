package com.airbng.platform.common.exception;

import com.airbng.platform.common.response.status.BaseResponseStatus;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Slf4j
public class S3Exception extends RuntimeException {
    private final BaseResponseStatus baseResponseStatus;

    public S3Exception(BaseResponseStatus status) {
        super(status.getMessage());
        this.baseResponseStatus = status;
        log.info("{} - message : {}", this.getClass().getSimpleName(), status.getMessage());
    }

    public S3Exception(BaseResponseStatus status, String detailMessage) {
        super(status.getMessage());
        this.baseResponseStatus = status;
        log.info("{} - message : {}", this.getClass().getSimpleName(), detailMessage);
    }


}
