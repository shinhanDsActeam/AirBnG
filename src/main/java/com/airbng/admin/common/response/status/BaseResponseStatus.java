package com.airbng.admin.common.response.status;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum BaseResponseStatus implements ResponseStatus {

    /**
     * 1000: 기본 응답 코드
     */
    SUCCESS(1000,HttpStatus.OK.value(), "요청에 성공하였습니다."),
    FAILURE(1001, HttpStatus.BAD_REQUEST.value(), "요청에 실패하였습니다."),
    INVALID_FIELD(1002, HttpStatus.BAD_REQUEST.value(), "요청한 값이 유효성 검사 조건을 만족하지 않습니다."),
    INVALID_JSON_FORMAT(1003, HttpStatus.BAD_REQUEST.value(), "요청한 JSON 필드의 데이터 타입이 올바르지 않습니다."),
    INVALID_JSON_SYNTAX(1004, HttpStatus.BAD_REQUEST.value(), "요청 JSON의 문법이 올바르지 않습니다."),
    TYPE_MISMATCH_PARAMETER(1005, HttpStatus.BAD_REQUEST.value(), "요청한 파라미터 값이 타입에 맞지 않습니다."),
    INVALID_DATETIME_FORMAT(1006, HttpStatus.BAD_REQUEST.value(), "날짜 형식이 올바르지 않습니다. (yyyy-MM-dd HH:mm:ss)"),
    INVALID_PARAMETER(1007, HttpStatus.BAD_REQUEST.value(), "요청한 파라미터 값의 유효성 검사 조건을 만족하지 않습니다."),
    UNSUPPORTED_MEDIA_TYPE(1008, HttpStatus.UNSUPPORTED_MEDIA_TYPE.value(), "지원하지 않는 요청 형식입니다."),

    /**
     * 2000 맴버 관련 코드
     */
    SUCCESS_LOGIN(2000, HttpStatus.OK.value(), "로그인에 성공하였습니다."),
    NOT_FOUND_MEMBER(2001, HttpStatus.NOT_FOUND.value(), "존재하지 않는 멤버입니다."),

    /**
     * 3000 락커 관련 코드
     */
    NOT_FOUND_LOCKERDETAILS(3002,HttpStatus.BAD_REQUEST.value(), "락커를 찾을 수 없습니다."),
    CANNOT_UPDATE_STATE(3003,HttpStatus.BAD_REQUEST.value(), "상태를 변경할 수 없습니다."),
    NOT_FOUND_LOCKER(3004,HttpStatus.BAD_REQUEST.value(), "보관소를 찾을 수 없습니다."),
    NOT_MEMBER_OF_LOCKER(3005,HttpStatus.BAD_REQUEST.value(), "해당 보관소의 작성자가 아닙니다."),

    ;

    private final int code;
    private final int httpStatus;
    private final String message;

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public int getHttpStatus() {
        return httpStatus;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
