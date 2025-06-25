package com.airbng.domain.base;

import com.airbng.common.exception.ReservationException;
import static com.airbng.common.response.status.BaseResponseStatus.CANNOT_UPDATE_STATE;

public enum ReservationState {
    CONFIRMED,
    PENDING,
    CANCELLED,
    COMPLETED;

    public void isAvailableUpdate(ReservationState state){
        if(ReservationState.COMPLETED.equals(state)||ReservationState.CANCELLED.equals(state)) throw new ReservationException(CANNOT_UPDATE_STATE);
    }
}