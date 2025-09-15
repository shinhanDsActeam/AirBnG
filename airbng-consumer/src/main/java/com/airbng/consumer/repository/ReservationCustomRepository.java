package com.airbng.consumer.repository;

import com.airbng.consumer.domain.base.ReservationState;
import com.airbng.consumer.domain.base.MemberRole;
import com.airbng.consumer.dto.reservation.ReservationSearchResponse;

import java.util.List;

public interface ReservationCustomRepository {

    List<ReservationSearchResponse> findAllReservationByMemberIdWithCursor(Long memberId, MemberRole role, List<ReservationState> states, Long nextCursorId, Long limit, String period, boolean isHistoryTab);
}
