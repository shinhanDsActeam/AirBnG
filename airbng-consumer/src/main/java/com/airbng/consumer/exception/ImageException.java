package com.airbng.consumer.exception;

import com.airbng.platform.common.exception.DomainException;
import com.airbng.platform.common.response.status.BaseResponseStatus;
import lombok.Getter;

@Getter
public class ImageException extends DomainException {
    public ImageException(BaseResponseStatus baseResponseStatus) {
        super(baseResponseStatus);
    }
    public ImageException(BaseResponseStatus baseResponseStatus, String detailMessage) {
        super(baseResponseStatus, detailMessage);
    }
}
