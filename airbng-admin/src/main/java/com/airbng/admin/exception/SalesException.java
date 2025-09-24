package com.airbng.admin.exception;

import com.airbng.platform.common.response.status.BaseResponseStatus;
import lombok.Getter;

@Getter
public class SalesException extends RuntimeException {
  public SalesException(BaseResponseStatus status) {
    super(String.valueOf(status));
  }
}
