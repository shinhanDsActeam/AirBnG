package com.airbng.common.exception_handler;

import com.airbng.common.exception.ReservationException;
import com.airbng.common.response.BaseErrorResponse;
import com.airbng.common.response.status.BaseExceptionResponseStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ReservationExceptionController {
    @ExceptionHandler(ReservationException.class)
    public ResponseEntity<BaseErrorResponse> handle(ReservationException ex) {
        return ResponseEntity
                .status(BaseExceptionResponseStatus.FAILURE.getStatus())
                .body(new BaseErrorResponse(BaseExceptionResponseStatus.FAILURE, ex.getMessage()));
    }
}
