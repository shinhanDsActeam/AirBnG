package com.airbng.service;

import com.airbng.common.response.status.BaseResponseStatus;
import com.airbng.dto.ReservationSearchResponse;
import com.airbng.dto.reservation.ReservationInsertRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ReservationService {

    List<ReservationSearchResponse> findAllReservationById(Long memberId, String role, String state, Long cursorId);

    // 예약 등록
    BaseResponseStatus insertReservation(ReservationInsertRequest request);

}
