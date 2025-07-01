package com.airbng.service;

import com.airbng.common.exception.ReservationException;
import com.airbng.domain.base.ReservationState;
import com.airbng.dto.reservation.ReservationPaging;
import com.airbng.dto.reservation.ReservationSearchResponse;
import com.airbng.mappers.ReservationMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationSearchServiceTest {

    @Mock
    private ReservationMapper reservationMapper;

    @InjectMocks
    private ReservationServiceImpl reservationService;

    private final Long memberId = 3L;
    private final String role = "DROPPER";
    private final List<ReservationState> state = Arrays.asList(ReservationState.COMPLETED, ReservationState.CANCELLED);
    private static final Long LIMIT = 10L;

    private List<ReservationSearchResponse> stubReservations;

    @BeforeEach
    void setup() {
        stubReservations = new ArrayList<>();
        for (long i = 109; i >= 100; i--) {
            ReservationSearchResponse res = new ReservationSearchResponse();
            res.setReservationId(i);
            stubReservations.add(res);
        }
    }

    @Nested
    @DisplayName("기간 필터링 테스트")
    class PeriodFilterTests {


        @Test
        @DisplayName("1주 필터")
        void testPeriod_1W() {
            testPeriod("1W");
        }

        @Test
        @DisplayName("3개월 필터")
        void testPeriod_3M() {
            testPeriod("3M");
        }

        @Test
        @DisplayName("6개월 필터")
        void testPeriod_6M() {
            testPeriod("6M");
        }

        @Test
        @DisplayName("1년 필터")
        void testPeriod_1Y() {
            testPeriod("1Y");
        }

        @Test
        @DisplayName("2년 필터")
        void testPeriod_2Y() {
            testPeriod("2Y");
        }

        @Test
        @DisplayName("전체 기간 필터 (ALL)")
        void testPeriod_ALL() {
            when(reservationMapper.findAllReservationById(
                    eq(memberId), eq(role), eq(state), eq(-1L), eq(LIMIT + 1), eq("ALL")
            )).thenReturn(stubReservations);


            when(reservationMapper.findReservationByMemberId(eq(memberId), eq(role))).thenReturn(1000L);

            ReservationPaging result = reservationService.findAllReservationById(
                    memberId, role, state, -1L, "ALL");

            assertNotNull(result);
            assertEquals(10, result.getReservations().size());
            assertEquals(-1L, result.getNextCursorId());

            verify(reservationMapper).findAllReservationById(
                    eq(memberId), eq(role), eq(state), eq(-1L), eq(LIMIT + 1), eq("ALL")
            );
        }

        void testPeriod(String period) {
            when(reservationMapper.findAllReservationById(
                    eq(memberId), eq(role), eq(state), eq(-1L), eq(LIMIT + 1), eq(period)
            )).thenReturn(stubReservations);

            when(reservationMapper.findReservationByMemberId(eq(memberId), eq(role))).thenReturn(1000L);

            ReservationPaging result = reservationService.findAllReservationById(
                    memberId, role, state, -1L, period);

            assertNotNull(result);
            assertEquals(10, result.getReservations().size());
            assertEquals(-1L, result.getNextCursorId());

            verify(reservationMapper).findAllReservationById(
                    eq(memberId), eq(role), eq(state), eq(-1L), eq(LIMIT + 1), eq(period)
            );
        }
    }

    @Test
    @DisplayName("예약 결과 없음 예외")
    void 예약_결과_없음() {
        String period = "1W";

        when(reservationMapper.findAllReservationById(
                eq(memberId), eq(role), eq(state), eq(-1L), eq(LIMIT + 1), eq(period)
        )).thenReturn(Collections.emptyList());


        assertThrows(ReservationException.class, () ->
                reservationService.findAllReservationById(memberId, role, state, -1L, period));
    }
}
