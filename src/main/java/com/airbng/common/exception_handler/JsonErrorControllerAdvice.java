package com.airbng.common.exception_handler;

import com.airbng.common.response.BaseErrorResponse;
import com.airbng.common.response.FieldValidationError;
import com.airbng.common.response.JsonSyntaxError;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.util.ContentCachingRequestWrapper;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import static com.airbng.common.response.status.BaseResponseStatus.INVALID_JSON_FORMAT;
import static com.airbng.common.response.status.BaseResponseStatus.INVALID_JSON_SYNTAX;

@Slf4j
@RestControllerAdvice(annotations = RestController.class)
public class JsonErrorControllerAdvice {

    /** Json 역직렬화 오류 - Json 파싱 오류 */
    @ExceptionHandler(JsonParseException.class)
    public ResponseEntity<BaseErrorResponse> handleJsonParseError(JsonParseException ex, HttpServletRequest request) {
        log.info("[JsonErrorControllerAdvice] JsonParseException: {}", ex.getMessage());
        String requestBody = getRequestBody(request);
        log.info("[JsonErrorControllerAdvice] Request Body: {}", requestBody);
        JsonSyntaxError error = extractErrorLineFromJson(requestBody, ex);

        return ResponseEntity
                .status(INVALID_JSON_SYNTAX.getHttpStatus())
                .body(new BaseErrorResponse(INVALID_JSON_SYNTAX, error));
    }

    /** Json 역직렬화 오류 - DTO 맵핑 실패 (타입 미스매치) */
    @ExceptionHandler(InvalidFormatException.class)
    public ResponseEntity<BaseErrorResponse> handleInvalidFormatException(InvalidFormatException ex) {
        log.info("[JsonErrorControllerAdvice] InvalidFormatException: {}", ex.getMessage());

        FieldValidationError error = FieldValidationError.builder()
                .fieldName(extractFieldName(ex.getPath().toString()))
                .rejectValue(ex.getValue().toString())
                .message("JSON 역직렬화 오류 - " + ex.getTargetType().getTypeName() + " 형식이어야 합니다.")
                .build();

        return ResponseEntity
                .status(INVALID_JSON_FORMAT.getHttpStatus())
                .body(new BaseErrorResponse<>(INVALID_JSON_FORMAT, error));

    }

    private static String extractFieldName(String path) {
        int startIndex = path.lastIndexOf("[\"");
        int endIndex = path.lastIndexOf("\"]");
        if (startIndex != -1 && endIndex != -1) {
            return path.substring(startIndex + 2, endIndex);
        }
        return path;
    }
  
    /** 캐싱해둔 request body 반환 */
    private static String getRequestBody(HttpServletRequest request) {
        if (request instanceof ContentCachingRequestWrapper) {
            ContentCachingRequestWrapper wrapper = (ContentCachingRequestWrapper) request;
            byte[] buf = wrapper.getContentAsByteArray();
            if (buf.length > 0) {
                try {
                    return new String(buf, wrapper.getCharacterEncoding());
                } catch (UnsupportedEncodingException e) {
                    return "[unreadable body: unsupported encoding]";
                }
            } else {
                return "[empty body]";
            }
        }
        return "[request is not wrapped - cannot read body]";
    }

    public JsonSyntaxError extractErrorLineFromJson(String requestBody, JsonParseException ex) {
        int errorLine = ex.getLocation().getLineNr();

        String[] lines = requestBody.split("\r?\n");
        String errorLineStr = lines[errorLine - 1];

        return JsonSyntaxError.builder()
                .lineNumber(errorLine)
                .rejectValue(errorLineStr)
                .message("JSON 문법 오류 - " + ex.getOriginalMessage())
                .build();
    }

}
