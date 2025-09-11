package com.airbng.common.exception;

import com.airbng.common.response.status.BaseResponseStatus;
import com.airbng.common.response.status.ResponseStatus;
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
