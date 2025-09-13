package com.airbng.admin.domain.base;

import com.airbng.common.exception.ReservationException;

import static com.airbng.common.response.status.BaseResponseStatus.CANNOT_UPDATE_STATE;

public enum ReviewStatus {
    WAITING,    //대기
    APPROVED,   //승인
    REJECTED;    //거절


    public void isAvailableUpdate(ReviewStatus state){
        if(ReviewStatus.APPROVED.equals(state)||ReviewStatus.REJECTED.equals(state))
            throw new ReservationException(CANNOT_UPDATE_STATE);
    }
}
