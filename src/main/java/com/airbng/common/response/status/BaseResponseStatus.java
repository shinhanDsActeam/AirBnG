package com.airbng.common.response.status;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum BaseResponseStatus implements ResponseStatus{

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


    /**
     * 2000 맴버 관련 코드
     */
    SUCCESS_LOGIN(2000, HttpStatus.OK.value(), "로그인에 성공하였습니다."),
    NOT_FOUND_MEMBER(2001, HttpStatus.NOT_FOUND.value(), "존재하지 않는 멤버입니다."),
    DUPLICATE_EMAIL(2002,HttpStatus.BAD_REQUEST.value(), "중복이메일 사용"),
    DUPLICATE_NICKNAME(2003,HttpStatus.BAD_REQUEST.value(),"중복닉네임 사용"),
    INVALID_PASSWORD(2004,HttpStatus.BAD_REQUEST.value(),"비밀번호 형식 오류"),
    DUPLICATE_PHONE(2005,HttpStatus.BAD_REQUEST.value(),"휴대폰 번호 중복 오류"),
    INVALID_EMAIL(2006, HttpStatus.BAD_REQUEST.value(),"이메일 형식을 지켜주세요"),
    INVALID_MEMBER(2007, HttpStatus.UNAUTHORIZED.value(), "유효하지 않은 멤버입니다."),

    /**
     * 3000 락커 관련 코드
     */
    NOT_FOUND_LOCKER(3000,HttpStatus.BAD_REQUEST.value(), "락커를 찾을 수 없습니다."),
    NOT_FOUND_LOCKERDETAILS(3001,HttpStatus.BAD_REQUEST.value(), "락커를 찾을 수 없습니다."),
    MEMBER_ALREADY_HAS_LOCKER(3002, HttpStatus.BAD_REQUEST.value(), "한 멤버당 하나의 보관소만 등록할 수 있습니다."),
    LOCKER_KEEPER_MISMATCH(3004, HttpStatus.BAD_REQUEST.value(), "선택한 보관소의 보관자 정보가 일치하지 않습니다."),
    /**
     * 4000 예약 관련 코드
     */
    CREATED_RESERVATION(4000, HttpStatus.CREATED.value(), "예약을 등록하였습니다."),
    CANNOT_CREATE_RESERVATION(4001, HttpStatus.BAD_REQUEST.value(), "예약을 등록할 수 없습니다."),
    NOT_FOUND_RESERVATION(4002, HttpStatus.NOT_FOUND.value(), "예약을 찾을 수 없습니다."),
    INVALID_RESERVATION_PARTICIPANTS(4003, HttpStatus.BAD_REQUEST.value(), "예약자와 보관자는 동일할 수 없습니다."),
    INVALID_RESERVATION_TIME(4004, HttpStatus.BAD_REQUEST.value(), "예약 시간은 시작 시간과 종료 시간이 모두 지정되어야 합니다."),
    INVALID_RESERVATION_TIME_ORDER(4005, HttpStatus.BAD_REQUEST.value(), "시작 시간은 종료 시간보다 작아야합니다."),
    CANNOT_UPDATE_STATE(4006,HttpStatus.BAD_REQUEST.value(), "예약 상태 변경에 실패하였습니다."),
    NOT_DROPPER_OF_RESERVATION(4007,HttpStatus.BAD_REQUEST.value(), "해당 예약의 드롭퍼가 아닙니다."),
    NOT_FOUND_EXPIRED_RESERVATION(4008,HttpStatus.NOT_FOUND.value(), "예약 만료 데이터를 조회할 수 없습니다."),
    NOT_FOUND_REMINDER_RESERVATION(4009,HttpStatus.NOT_FOUND.value(), "곧 종료 예정(30분 전) 예약 데이터를 조회할 수 없습니다."),
    NOT_FOUND_STATE_CHANGE_RESERVATION(4010,HttpStatus.NOT_FOUND.value(), "상태 변경(CONFIRMED)된 예약 데이터를 조회할 수 없습니다."),
    NOT_FOUND_CANCEL_NOTICE_RESERVATION(4011,HttpStatus.NOT_FOUND.value(), "상태 변경(CANCELLED)된 예약 데이터를 조회할 수 없습니다."),



    /**
     * 5000 짐 타입 관련 코드
     */
    INVALID_JIMTYPE(5000, HttpStatus.UNPROCESSABLE_ENTITY.value(), "존재하지 않는 짐 타입이 포함되어 있습니다."),
    INVALID_JIMTYPE_COUNT(5001, HttpStatus.BAD_REQUEST.value(), "요청한 짐 타입 개수와 실제 등록된 개수가 일치하지 않습니다."),
    LOCKER_DOES_NOT_SUPPORT_JIMTYPE(5002, HttpStatus.BAD_REQUEST.value(), "해당 보관소가 관리하지 않는 짐 타입입니다."),
    DUPLICATE_JIMTYPE(5003, HttpStatus.BAD_REQUEST.value(), "중복된 짐 타입이 존재합니다."),
    /**
     * 6000: image
     */
    UPLOAD_FAILED(6001, HttpStatus.INTERNAL_SERVER_ERROR.value(),"이미지 업로드에 실패하였습니다."),
    EMPTY_FILE(6002, HttpStatus.BAD_REQUEST.value(),"업로드할 이미지가 없습니다."),
    INVALID_EXTENSIONS(6003, HttpStatus.UNSUPPORTED_MEDIA_TYPE.value(),"허용되지 않는 파일 확장자입니다."),
    EXCEED_IMAGE_COUNT(6004, HttpStatus.PAYLOAD_TOO_LARGE.value(), "이미지 개수가 초과되었습니다. 최대 5개까지 업로드 가능합니다."),

    /**
     * 9000: sesssion
     */
    SESSION_NOT_FOUND(7000, HttpStatus.UNAUTHORIZED.value(), "세션이 존재하지 않습니다. 다시 로그인해주세요."),
    SESSION_INVALID_TYPE(7001, HttpStatus.UNAUTHORIZED.value(), "세션 정보가 올바르지 않습니다."),
    SESSION_EXPIRED(7002, HttpStatus.UNAUTHORIZED.value(), "세션이 만료되었습니다."),

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
