package com.airbng.admin.common.exception;

import com.airbng.admin.common.response.status.BaseResponseStatus;
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
