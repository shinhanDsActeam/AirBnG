package com.airbng.service;

import com.airbng.common.response.status.BaseResponseStatus;
import com.airbng.dto.reservation.ReservationInsertRequest;
import org.springframework.stereotype.Service;
import com.airbng.dto.reservation.ReservationCancelResponse;

@Service
public interface ReservationService {

    /**
     * 예약 취소 기능
     * */
    public ReservationCancelResponse updateReservationState(Long reservationId, Long memberId);
    // 예약 등록
    BaseResponseStatus insertReservation(ReservationInsertRequest request);

}