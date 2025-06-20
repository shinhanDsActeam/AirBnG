package com.airbng.service;

import com.airbng.dto.ReservationSearchResponse;

import java.util.List;

public interface ReservationService {
    List<ReservationSearchResponse> findAllReservationById(Long memberId, String role, String state, Long cursorId);
}
