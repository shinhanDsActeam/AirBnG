package com.airbng.service;

import com.airbng.common.exception.ReservationException;
import com.airbng.dto.LockerSearchResponse;
import com.airbng.dto.ReservationPaging;
import com.airbng.dto.ReservationSearchResponse;
import com.airbng.mappers.ReservationMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.airbng.common.response.status.BaseResponseStatus.NOT_FOUND_RESERVATION;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {

    private final ReservationMapper reservationMapper;

    @Override
    public ReservationPaging findAllReservationById(Long memberId, String role, String state, Long nextCursorId, Long limit) {
        log.info("Finding reservation by memberId: {}, role: {}, state: {}, nextCursorId: {}, limit:{} ",
                memberId, role, state, nextCursorId, limit);

        List<ReservationSearchResponse> reservations = reservationMapper.findAllReservationById(
                memberId, role, state, nextCursorId, limit + 1 //다음 페이지 유무 확인
        );

        // 예외 처리: 예약이 없을 경우
        if (reservations == null || reservations.isEmpty()) {
            throw new ReservationException(NOT_FOUND_RESERVATION);
        }

        boolean hasNextPage = reservations.size() > limit;
        List<ReservationSearchResponse> content = reservations.stream()
                .limit(limit)
                .collect(Collectors.toList());

        // role을 응답 DTO에 표시
        for (ReservationSearchResponse dto : content) {
            dto.setRole(role.toUpperCase()); // KEEPER or DROPPER
        }

        if (hasNextPage && !content.isEmpty()) {
            nextCursorId = content.get(content.size() - 1).getReservationId();
        } else {
            nextCursorId = -1L;  // 더 이상 페이지가 없으면 -1로 설정
        }

        ReservationPaging paging = ReservationPaging.builder()
                .reservations(content)
                .nextCursorId(nextCursorId)
                .hasNextPage(hasNextPage)
                .totalCount(reservationMapper.findReservationByMemberId(memberId, role))
                .build();
        return paging;
    }
}
