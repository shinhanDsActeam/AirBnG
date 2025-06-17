package com.airbng.common.exception_handler;

import com.airbng.common.exception.LockerException;
import com.airbng.common.exception.ReservationException;
import com.airbng.common.response.BaseErrorResponse;
import com.airbng.common.response.status.BaseResponseStatus;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class LockerExceptionController {
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(LockerException.class)
    public BaseErrorResponse locker_LockerException(LockerException e) {
        return new BaseErrorResponse(e.getExceptionStatus(), e.getMessage());
    }
}