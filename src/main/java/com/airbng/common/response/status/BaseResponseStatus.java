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
     * 2000: member
     * */
    MEMBER_NOT_FOUND(2001, HttpStatus.NOT_FOUND.value(), "존재하지 않는 멤버입니다."),
    DUPLICATE_EMAIL(2002,HttpStatus.BAD_REQUEST.value(), "중복이메일 사용"),
    DUPLICATE_NICKNAME(2003,HttpStatus.BAD_REQUEST.value(),"중복닉네임 사용"),
    INVALID_PASSWORD(2004,HttpStatus.BAD_REQUEST.value(),"비밀번호 형식 오류"),
    DUPLICATE_PHONE(2005,HttpStatus.BAD_REQUEST.value(),"휴대폰 번호 중복 오류"),


    /**
     * 3000 락커 관련 코드
     */
    NOT_FOUND_LOCKER(3000,HttpStatus.NOT_FOUND.value(), "락커를 찾을 수 없습니다."),
    NOT_FOUND_LOCKERDETAILS(3002,HttpStatus.NOT_FOUND.value(), "락커를 찾을 수 없습니다."),
    MEMBER_ALREADY_HAS_LOCKER(3001, HttpStatus.CONFLICT.value(), "한 멤버당 하나의 보관소만 등록할 수 있습니다."),
    INVALID_JIMTYPE(3006, HttpStatus.UNPROCESSABLE_ENTITY.value(), "존재하지 않는 짐 타입이 포함되어 있습니다."),
    DUPLICATE_JIMTYPE(3007, HttpStatus.UNPROCESSABLE_ENTITY.value(), "중복된 짐 타입이 존재합니다."),

    /**
     * 4000 예약 관련 코드
     */

    /**
     * 5000 짐 타입 관련 코드
     */

    /**
     * 6000: image
     */
    UPLOAD_FAILED(6001, HttpStatus.INTERNAL_SERVER_ERROR.value(),"이미지 업로드에 실패하였습니다."),
    EMPTY_FILE(6002, HttpStatus.BAD_REQUEST.value(),"업로드할 이미지가 없습니다."),
    INVALID_EXTENSIONS(6003, HttpStatus.UNSUPPORTED_MEDIA_TYPE.value(),"허용되지 않는 파일 확장자입니다."),
    EXCEED_IMAGE_COUNT(6004, HttpStatus.PAYLOAD_TOO_LARGE.value(), "이미지 개수가 초과되었습니다. 최대 5개까지 업로드 가능합니다.");


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
