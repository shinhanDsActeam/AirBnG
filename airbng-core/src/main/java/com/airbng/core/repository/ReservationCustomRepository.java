package com.airbng.core.repository;

import com.airbng.core.domain.base.ReservationState;
import com.airbng.core.domain.base.MemberRole;
import com.airbng.core.dto.reservation.ReservationSearchResponse;

import java.util.List;

public interface ReservationCustomRepository {

    List<ReservationSearchResponse> findAllReservationByMemberIdWithCursor(Long memberId, MemberRole role, List<ReservationState> states, Long nextCursorId, Long limit, String period, boolean isHistoryTab);
}
