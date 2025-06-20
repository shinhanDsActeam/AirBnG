package com.airbng.service;

import com.airbng.common.exception.ReservationException;
import com.airbng.dto.ReservationSearchResponse;
import com.airbng.mappers.ReservationMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.airbng.common.response.status.BaseResponseStatus.NOT_FOUND_RESERVATION;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {

    private final ReservationMapper reservationMapper;
    private List<ReservationSearchResponse> reservations;
    private final Long cursorId;
    private final boolean hasNextPage;

    @Override
    public List<ReservationSearchResponse> findAllReservationById(Long memberId, String role, String state, Long cursorId) {
        log.info("Finding reservation by memberId: {}, role: {}, state: {}, cursorId: {} ", memberId, role, state, cursorId);
        
//        // 예외처리 : 파라미터 유효성 검사
//        if (memberId == null || role == null || role.isEmpty()) {
//            throw new ReservationException(INVALID_RESERVATION_REQUEST);
//        }

        Map<String, Object> params = new HashMap<>();
        params.put("memberId", memberId);
        params.put("role", role);

        // state가 null이거나 빈 문자열인 경우, state 추가하지 않음
        if (state != null && !state.isEmpty()) {
            params.put("state", state);
        }

        List<ReservationSearchResponse> reservations = reservationMapper.findAllReservationById(params);
        
        // 예외 처리: 예약이 없을 경우
        if (reservations == null || reservations.isEmpty()) {
            throw new ReservationException(NOT_FOUND_RESERVATION);
        }

        // role을 응답 DTO에 표시
        for (ReservationSearchResponse dto : reservations) {
            dto.setRole(role.toUpperCase()); // KEEPER or DROPPER
        }

        return reservations;
    }
}
