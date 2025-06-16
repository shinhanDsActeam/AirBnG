package com.airbng.common.exception_handler;

import com.airbng.common.exception.LocationException;
import com.airbng.common.response.BaseErrorResponse;
import com.airbng.common.response.status.BaseResponseStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class LocationExceptionController {
    @ExceptionHandler(LocationException.class)
    public ResponseEntity<BaseErrorResponse> handle(LocationException ex) {
        return ResponseEntity
                .status(BaseResponseStatus.FAILURE.getStatus())
                .body(new BaseErrorResponse(BaseResponseStatus.FAILURE, ex.getMessage()));
    }
}
