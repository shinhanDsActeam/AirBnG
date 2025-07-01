package com.airbng.service;

import com.airbng.common.response.status.BaseResponseStatus;
import com.airbng.dto.reservation.*;
import com.airbng.domain.base.ReservationState;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ReservationService {

    // 예약 조회 + 페이징 처리
    ReservationPaging findAllReservationById(Long memberId, String role, Object state, Long nextCursorId, String period);

    /**
     * 예약 취소 기능
     * */
    public ReservationCancelResponse updateReservationState(Long reservationId, Long memberId);

    //예약 승인/거절
    ReservationConfirmResponse confirmReservationState(Long reservationId, String approve, Long memberId);

    // 예약 등록
    BaseResponseStatus insertReservation(ReservationInsertRequest request);

    //예약 상새
    ReservationDetailResponse findReservationDetail(Long reservationId, Long memberId);
}
