package com.airbng.service;

import com.airbng.common.response.status.BaseResponseStatus;
import com.airbng.dto.ReservationPaging;
import com.airbng.dto.reservation.ReservationInsertRequest;
import org.springframework.stereotype.Service;

@Service
public interface ReservationService {

    ReservationPaging findAllReservationById(Long memberId, String role, String state, Long nextCursorId, Long limit);

    // 예약 등록
    BaseResponseStatus insertReservation(ReservationInsertRequest request);
}
