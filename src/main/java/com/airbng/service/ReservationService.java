package com.airbng.service;

import com.airbng.common.response.status.BaseResponseStatus;
import com.airbng.domain.base.ReservationState;
import com.airbng.dto.reservation.ReservationInsertRequest;
import com.airbng.dto.reservation.ReservationPaging;
import org.springframework.stereotype.Service;
import com.airbng.dto.reservation.ReservationCancelResponse;

@Service
public interface ReservationService {

    // 예약 조회 + 페이징 처리
    ReservationPaging findAllReservationById(Long memberId, String role, ReservationState state, Long nextCursorId, String period);

    /**
     * 예약 취소 기능
     * */
    public ReservationCancelResponse updateReservationState(Long reservationId, Long memberId);

    // 예약 등록
    BaseResponseStatus insertReservation(ReservationInsertRequest request);
}
