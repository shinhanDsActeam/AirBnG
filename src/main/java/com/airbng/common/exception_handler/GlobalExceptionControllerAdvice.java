package com.airbng.common.exception_handler;

import com.airbng.common.exception.DomainException;
import com.airbng.common.response.BaseErrorResponse;
import com.airbng.common.response.FieldValidationError;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.format.DateTimeParseException;

import static com.airbng.common.response.status.BaseResponseStatus.INVALID_DATETIME_FORMAT;

@RestControllerAdvice
public class GlobalExceptionControllerAdvice {

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<BaseErrorResponse> handleDomainException(DomainException ex) {
        return ResponseEntity
                .status(ex.getBaseResponseStatus().getHttpStatus())
                .body(new BaseErrorResponse(ex.getBaseResponseStatus(), ex.getMessage()));
    }

    @ExceptionHandler(DateTimeParseException.class)
    public ResponseEntity<BaseErrorResponse> handleDateTimeParseException(DateTimeParseException ex) {
        FieldValidationError error = FieldValidationError.builder()
                .fieldName("dateTime")
                .rejectValue(ex.getParsedString())
                .message(ex.getMessage())
                .build();

        return ResponseEntity
                .status(INVALID_DATETIME_FORMAT.getHttpStatus())
                .body(new BaseErrorResponse(INVALID_DATETIME_FORMAT, error));
    }
}
