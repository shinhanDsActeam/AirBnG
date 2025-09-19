package com.airbng.consumer.domain.base;

import com.airbng.consumer.exception.ReservationException;

import static com.airbng.platform.common.response.status.BaseResponseStatus.CANNOT_UPDATE_STATE;

public enum ReservationState {
    CONFIRMED, //확정 (호스트가 승인)
    PENDING, //대기 (사용자가 신청)
    REJECTED, //거절 (호스트가 거절)
    CANCELLED, //취소
    COMPLETED; //완료

    public void isAvailableUpdate(ReservationState state){
        if(ReservationState.COMPLETED.equals(state)||ReservationState.CANCELLED.equals(state))
            throw new ReservationException(CANNOT_UPDATE_STATE);
    }
}