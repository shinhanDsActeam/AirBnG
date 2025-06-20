package com.airbng.service;

import com.airbng.dto.ReservationCancelResponse;

public interface ReservationService {

    /**
     * 예약 취소 기능
     * */
    public ReservationCancelResponse updateReservationState(Long reservationId);

}
