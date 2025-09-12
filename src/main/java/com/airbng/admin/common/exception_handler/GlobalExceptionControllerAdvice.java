package com.airbng.admin.common.exception_handler;

import com.airbng.admin.common.exception.DomainException;
import com.airbng.admin.common.response.BaseErrorResponse;
import com.airbng.admin.common.response.FieldValidationError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.format.DateTimeParseException;

import static com.airbng.common.response.status.BaseResponseStatus.INVALID_DATETIME_FORMAT;
import static com.airbng.common.response.status.BaseResponseStatus.UNSUPPORTED_MEDIA_TYPE;

@RestControllerAdvice
public class GlobalExceptionControllerAdvice {

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<BaseErrorResponse<Object>> handleDomainException(DomainException ex) {
        return ResponseEntity
                .status(ex.getBaseResponseStatus().getHttpStatus())
                .body(new BaseErrorResponse<>(ex.getBaseResponseStatus(), ex.getMessage()));
    }

    @ExceptionHandler(DateTimeParseException.class)
    public ResponseEntity<BaseErrorResponse<FieldValidationError>> handleDateTimeParseException(DateTimeParseException ex) {
        FieldValidationError error = FieldValidationError.builder()
                .fieldName("dateTime")
                .rejectValue(ex.getParsedString())
                .message(ex.getMessage())
                .build();

        return ResponseEntity
                .status(INVALID_DATETIME_FORMAT.getHttpStatus())
                .body(new BaseErrorResponse<>(INVALID_DATETIME_FORMAT, error));
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<BaseErrorResponse<Object>> handleHttpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException ex) {
        return ResponseEntity
                .status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                .body(new BaseErrorResponse<>(UNSUPPORTED_MEDIA_TYPE, ex.getMessage()));
    }
}
