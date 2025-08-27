package com.airbng.repository;

import com.airbng.domain.base.ReservationState;
import com.airbng.domain.base.MemberRole;
import com.airbng.dto.reservation.ReservationSearchResponse;

import java.util.List;

public interface ReservationCustomRepository {

    List<ReservationSearchResponse> findAllReservationByMemberIdWithCursor(Long memberId, MemberRole role, List<ReservationState> states, Long nextCursorId, Long limit, String period, boolean isHistoryTab);
}
