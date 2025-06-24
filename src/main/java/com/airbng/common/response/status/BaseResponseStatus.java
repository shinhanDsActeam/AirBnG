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
    INVALID_FIELD(1002, HttpStatus.BAD_REQUEST.value(), "요청 본문에 잘못된 형식의 필드가 포함되어 있습니다."),
    INVALID_JSON_FORMAT(1003, HttpStatus.BAD_REQUEST.value(), "요청한 JSON 필드의 데이터 타입이 올바르지 않습니다."),
    INVALID_JSON_SYNTAX(1004, HttpStatus.BAD_REQUEST.value(), "요청 JSON의 문법이 올바르지 않습니다."),
    INVALID_PARAMETER(1005, HttpStatus.BAD_REQUEST.value(), "요청한 파라미터 값이 타입에 맞지 않습니다."),
    INVALID_DATETIME_FORMAT(1006, HttpStatus.BAD_REQUEST.value(), "날짜 형식이 올바르지 않습니다. (yyyy-MM-dd HH:mm:ss)"),

    /**
     * 2000 맴버 관련 코드
     */
    NOT_FOUND_MEMBER(2001, HttpStatus.NOT_FOUND.value(), "존재하지 않는 멤버입니다."),
    DUPLICATE_EMAIL(2002,HttpStatus.BAD_REQUEST.value(), "중복이메일 사용"),
    DUPLICATE_NICKNAME(2003,HttpStatus.BAD_REQUEST.value(),"중복닉네임 사용"),
    INVALID_PASSWORD(2004,HttpStatus.BAD_REQUEST.value(),"비밀번호 형식 오류"),
    DUPLICATE_PHONE(2005,HttpStatus.BAD_REQUEST.value(),"휴대폰 번호 중복 오류"),
    INVALID_EMAIL(2006, HttpStatus.BAD_REQUEST.value(),"이메일 형식 오류"),
    INVALID_MEMBER(2007, HttpStatus.UNAUTHORIZED.value(), "유효하지 않은 멤버입니다."),


    /**
     * 3000 락커 관련 코드
     */
    NOT_FOUND_LOCKER(3001,HttpStatus.BAD_REQUEST.value(), "락커를 찾을 수 없습니다."),
    NOT_FOUND_LOCKERDETAILS(3002,HttpStatus.BAD_REQUEST.value(), "락커를 찾을 수 없습니다."),
    MEMBER_ALREADY_HAS_LOCKER(3003, HttpStatus.BAD_REQUEST.value(), "한 멤버당 하나의 보관소만 등록할 수 있습니다."),
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

    /**
     * 5000 짐 타입 관련 코드
     */
    INVALID_JIMTYPE(5001, HttpStatus.UNPROCESSABLE_ENTITY.value(), "존재하지 않는 짐 타입이 포함되어 있습니다."),
    INVALID_JIMTYPE_COUNT(5002, HttpStatus.BAD_REQUEST.value(), "요청한 짐 타입 개수와 실제 등록된 개수가 일치하지 않습니다."),
    LOCKER_DOES_NOT_SUPPORT_JIMTYPE(5003, HttpStatus.BAD_REQUEST.value(), "해당 보관소가 관리하지 않는 짐 타입입니다."),
    DUPLICATE_JIMTYPE(5004, HttpStatus.BAD_REQUEST.value(), "중복된 짐 타입이 존재합니다."),
    /**
     * 6000: image
     */
    UPLOAD_FAILED(6001, HttpStatus.INTERNAL_SERVER_ERROR.value(),"이미지 업로드에 실패하였습니다."),
    EMPTY_FILE(6002, HttpStatus.BAD_REQUEST.value(),"업로드할 이미지가 없습니다."),
    INVALID_EXTENSIONS(6003, HttpStatus.UNSUPPORTED_MEDIA_TYPE.value(),"허용되지 않는 파일 확장자입니다."),
    EXCEED_IMAGE_COUNT(6004, HttpStatus.PAYLOAD_TOO_LARGE.value(), "이미지 개수가 초과되었습니다. 최대 5개까지 업로드 가능합니다."),

    /**
     * 7000: 찜 관련 코드
     */
    // 찜 등록 성공
    SUCCESS_INSERT_ZZIM(7000, HttpStatus.OK.value(), "찜 등록에 성공하였습니다."),
    SUCCESS_DELETE_ZZIM(7001, HttpStatus.OK.value(), "찜 취소에 성공하였습니다."),
    DUPLICATE_ZZIM(7002, HttpStatus.BAD_REQUEST.value(), "이미 찜한 락커입니다."),
    SELF_LOCKER_ZZIM(7003, HttpStatus.BAD_REQUEST.value(), "자신의 보관소는 찜할 수 없습니다."),

    /**
     * 8000: DDOS / 보안 관련 코드
     */
    DDOS_PREVENTION(8000, HttpStatus.TOO_MANY_REQUESTS.value(), "요청이 너무 빠릅니다. 잠시 후 다시 시도해주세요."),

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