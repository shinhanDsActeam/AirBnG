package com.airbng.common.exception_handler;

import com.airbng.common.exception.ImageException;
import com.airbng.common.response.BaseErrorResponse;
import com.airbng.common.response.status.BaseResponseStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ImageExceptionController {
    @ExceptionHandler(ImageException.class)
    public ResponseEntity<BaseErrorResponse> handle(ImageException ex) {
        return ResponseEntity
                .status(BaseResponseStatus.FAILURE.getStatus())
                .body(new BaseErrorResponse(BaseResponseStatus.FAILURE, ex.getMessage()));
    }
}
