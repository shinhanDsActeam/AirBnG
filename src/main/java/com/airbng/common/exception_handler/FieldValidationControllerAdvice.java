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
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.util.ContentCachingRequestWrapper;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.airbng.common.response.status.BaseResponseStatus.*;

@Slf4j
@RestControllerAdvice(annotations = RestController.class)
public class FieldValidationControllerAdvice {

    /**
     * 바인딩 후 발생하는 유효성 검증 예외
     */
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

    /**
     * 바인딩 전에 발생하는 예외<br>
     * -> 예외 자체는 validation 전에 발생
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<BaseErrorResponse> handleTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        log.info("[FieldValidationControllerAdvice] MethodArgumentTypeMismatchException");

        FieldValidationError build = FieldValidationError.builder()
                .fieldName(ex.getName())
                .rejectValue((String) ex.getValue())
                .message(ex.getMessage())
                .build();

        return ResponseEntity
                .status(INVALID_PARAMETER.getHttpStatus())
                .body(new BaseErrorResponse(INVALID_PARAMETER, build));
    }

    /**
     * @Validated 에서 발생하는 예외
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<BaseErrorResponse> handleConstraintViolationException(ConstraintViolationException ex, HttpServletRequest request) {
        log.info("[FieldValidationControllerAdvice] ConstraintViolationException");

        List<String> parameterNames;
        if (request instanceof ContentCachingRequestWrapper) {
            ContentCachingRequestWrapper wrapper = (ContentCachingRequestWrapper) request;
            parameterNames = Collections.list(wrapper.getParameterNames());
        } else {
            parameterNames = Collections.emptyList();
        }

        FieldErrors errors = FieldErrors.builder()
                .errors(
                        ex.getConstraintViolations().stream().map(
                                violation -> {
                                    int idx = extractArgIndex(violation.getPropertyPath().toString());
                                    return FieldValidationError.builder()
                                            .fieldName(parameterNames.get(idx))
                                            .rejectValue(violation.getInvalidValue().toString())
                                            .message(violation.getMessage())
                                            .build();
                                }
                        ).collect(Collectors.toList())
                ).build();

        return ResponseEntity
                .status(VALIDATION_FAILED.getHttpStatus())
                .body(new BaseErrorResponse(VALIDATION_FAILED, errors));
    }

    // violation의 PropertyPath에서 Argument 인덱스를 추출하는 메소드
    public static int extractArgIndex(String propertyPath) {
        Pattern pattern = Pattern.compile("\\.arg(\\d+)$");
        Matcher matcher = pattern.matcher(propertyPath);
        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1));
        }
        throw new IllegalArgumentException("No arg index found in property path: " + propertyPath);
    }
}
