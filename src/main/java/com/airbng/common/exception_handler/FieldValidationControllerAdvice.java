package com.airbng.common.exception_handler;

import com.airbng.common.response.BaseErrorResponse;
import com.airbng.common.response.BaseResponse;
import com.airbng.common.response.FieldErrors;
import com.airbng.common.response.FieldValidationError;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

import static com.airbng.common.response.status.BaseResponseStatus.INVALID_FIELD;

@Slf4j
@RestControllerAdvice(annotations = RestController.class)
public class FieldValidationControllerAdvice {

    /** 바인딩 후 발생하는 유효성 검증 예외 */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<BaseResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        log.info("[FieldValidationControllerAdvice] MethodArgumentNotValidException");

        List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();

        FieldErrors build = FieldErrors.builder().errors(
                        fieldErrors.stream().map(fieldError ->
                                new FieldValidationError(
                                        fieldError.getField(),
                                        String.valueOf(fieldError.getRejectedValue()),
                                        fieldError.getDefaultMessage())
                        ).collect(Collectors.toList()))
                .build();

        return ResponseEntity
                .status(INVALID_FIELD.getHttpStatus())
                .body(new BaseResponse(INVALID_FIELD, build));
    }

}
