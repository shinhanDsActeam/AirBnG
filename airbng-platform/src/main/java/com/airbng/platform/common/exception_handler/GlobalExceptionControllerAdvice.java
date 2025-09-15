package com.airbng.platform.common.exception_handler;

import com.airbng.platform.common.exception.DomainException;
import com.airbng.platform.common.exception.S3Exception;
import com.airbng.platform.common.response.BaseErrorResponse;
import com.airbng.platform.common.response.FieldValidationError;
import com.airbng.platform.common.response.status.BaseResponseStatus;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.format.DateTimeParseException;

@RestControllerAdvice
public class GlobalExceptionControllerAdvice {

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<BaseErrorResponse<Object>> handleDomainException(DomainException ex) {
        return ResponseEntity
                .status(ex.getBaseResponseStatus().getHttpStatus())
                .body(new BaseErrorResponse<>(ex.getBaseResponseStatus(), ex.getMessage()));
    }

    @ExceptionHandler(S3Exception.class)
    public ResponseEntity<BaseErrorResponse<Object>> handleS3Exception(S3Exception ex) {
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
                .status(BaseResponseStatus.INVALID_DATETIME_FORMAT.getHttpStatus())
                .body(new BaseErrorResponse<>(BaseResponseStatus.INVALID_DATETIME_FORMAT, error));
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<BaseErrorResponse<Object>> handleHttpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException ex) {
        return ResponseEntity
                .status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                .body(new BaseErrorResponse<>(BaseResponseStatus.UNSUPPORTED_MEDIA_TYPE, ex.getMessage()));
    }
}
