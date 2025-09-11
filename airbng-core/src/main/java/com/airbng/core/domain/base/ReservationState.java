package com.airbng.core.domain.base;

import com.airbng.core.exception.ReservationException;

import static com.airbng.platform.common.response.status.BaseResponseStatus.CANNOT_UPDATE_STATE;

public enum ReservationState {
    CONFIRMED, //확정
    PENDING, //대기
    CANCELLED, //취소
    COMPLETED; //완료

    public void isAvailableUpdate(ReservationState state){
        if(ReservationState.COMPLETED.equals(state)||ReservationState.CANCELLED.equals(state))
            throw new ReservationException(CANNOT_UPDATE_STATE);
    }
}