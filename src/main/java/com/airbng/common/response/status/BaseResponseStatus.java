package com.airbng.common.response.status;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum BaseResponseStatus implements ResponseStatus{

    /**
     * 1000: 요청 성공 (OK)
     */
    SUCCESS(1000,HttpStatus.OK.value(), "요청에 성공하였습니다."),
    FAILURE(2000, HttpStatus.BAD_REQUEST.value(), "요청에 실패하였습니다."),

    /**
     * 2000 맴버 관련 코드
     */

    /**
     * 3000 락커 관련 코드
     */
    NOT_FOUND_LOCKER(3000,HttpStatus.NO_CONTENT.value(), "락커를 찾을 수 없습니다."),
    MEMBER_ALREADY_HAS_LOCKER(3001, HttpStatus.CONFLICT.value(), "한 멤버당 하나의 보관소만 등록할 수 있습니다."),
    INVALID_JIMTYPE(3006, HttpStatus.BAD_REQUEST.value(), "존재하지 않는 짐 타입이 포함되어 있습니다."),
    DUPLICATE_JIMTYPE(3007, HttpStatus.UNPROCESSABLE_ENTITY.value(), "중복된 짐 타입이 존재합니다.");

    /**
     * 4000 예약 관련 코드
     */

    /**
     * 5000 짐 타입 관련 코드
     */


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