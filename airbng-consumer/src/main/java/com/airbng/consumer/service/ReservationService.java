package com.airbng.consumer.service;

import com.airbng.consumer.domain.base.MemberRole;
import com.airbng.consumer.dto.reservation.*;
import com.airbng.platform.security.principal.AirbngPrincipal;
import org.springframework.stereotype.Service;

@Service
public interface ReservationService {

    // 예약 조회 + 페이징 처리
    ReservationPaging findAllReservationById(Long memberId, MemberRole role, Object state, Long nextCursorId, String period);

    /**
     * 예약 취소 기능
     * */
    ReservationCancelResponse cancelReservation(Long reservationId, Long memberId);

    //예약 승인/거절
    ReservationConfirmResponse confirmReservation(Long reservationId, boolean approve, Long memberId);

    // 예약 폼 데이터 받아오기
    ReservationFormResponse getReservationForm(Long lockerId);

    // 예약 등록
    ReservationInsertResponse insertReservation(String idemKey, ReservationInsertRequest request, AirbngPrincipal principal);

    //예약 상새
    ReservationDetailResponse findReservationDetail(Long reservationId, Long memberId);
    void deleteReservationById(Long reservationId);
}
