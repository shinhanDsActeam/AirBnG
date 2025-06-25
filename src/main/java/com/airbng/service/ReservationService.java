package com.airbng.service;

import com.airbng.common.response.status.BaseResponseStatus;
import com.airbng.dto.reservation.ReservationDetailResponse;
import com.airbng.dto.reservation.ReservationInsertRequest;
import org.springframework.stereotype.Service;

@Service
public interface ReservationService {

    // 예약 등록
    BaseResponseStatus insertReservation(ReservationInsertRequest request);

    //예약 상새
    ReservationDetailResponse findReservationDetail(Long reservationId, Long memberId);

}