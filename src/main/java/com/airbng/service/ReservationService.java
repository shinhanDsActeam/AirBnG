package com.airbng.service;

import com.airbng.dto.ReservationCancelRequest;
import com.airbng.dto.ReservationCancelResponse;
import org.springframework.web.bind.annotation.PathVariable;

public interface ReservationService {

    /**
     * 예약 취소 기능
     * */
    public ReservationCancelResponse updateReservationState(Long reservationId, Long memberId);

}
