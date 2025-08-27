package com.airbng.repository;

import com.airbng.domain.base.ReservationState;
import com.airbng.dto.reservation.ReservationSearchResponse;

import java.util.List;
import java.util.Map;

public interface ReservationCustomRepository {

    List<ReservationSearchResponse> findAllReservationByMemberIdWithCursor(Long memberId, String role, List<ReservationState> states, Long nextCursorId, Long limit, String period, boolean isHistoryTab);
}
