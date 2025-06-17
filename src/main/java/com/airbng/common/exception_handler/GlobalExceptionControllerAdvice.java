package com.airbng.common.exception_handler;

import com.airbng.common.exception.DomainException;
import com.airbng.common.exception.ImageException;
import com.airbng.common.response.BaseErrorResponse;
import com.airbng.common.response.status.BaseResponseStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionControllerAdvice {

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<BaseErrorResponse> handle(DomainException ex) {
        return ResponseEntity
                .status(ex.getBaseResponseStatus().getHttpStatus())
                .body(new BaseErrorResponse(ex.getBaseResponseStatus(), ex.getMessage()));
    }
}
