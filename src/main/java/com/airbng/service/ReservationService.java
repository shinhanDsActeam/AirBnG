package com.airbng.service;

import com.airbng.dto.ReservationPaging;
import com.airbng.dto.ReservationSearchResponse;

import java.util.List;

public interface ReservationService {
    ReservationPaging findAllReservationById(Long memberId, String role, String state, Long nextCursorId, Long limit);
}
