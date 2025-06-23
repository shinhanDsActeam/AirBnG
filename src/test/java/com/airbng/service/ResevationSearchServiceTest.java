package com.airbng.service;

import com.airbng.common.exception.ReservationException;
import com.airbng.dto.reservation.ReservationPaging;
import com.airbng.dto.reservation.ReservationSearchResponse;
import com.airbng.mappers.ReservationMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class ResevationSearchServiceTest {

    @Mock
    private ReservationMapper reservationMapper;
    private final Long memberId = 3L;
    private final String role = "DROPPER";
    private final String state = "CONFIRMED";
    private final Long limit = 2L;

    @InjectMocks
    private ReservationServiceImpl reservationService;

    ReservationSearchResponse SearchResponse1, SearchResponse2;

    @BeforeEach
    void setup() {
        SearchResponse1 = new ReservationSearchResponse();
        SearchResponse1.setReservationId(101L);

        SearchResponse2 = new ReservationSearchResponse();
        SearchResponse2.setReservationId(100L);
    }

    @Nested
    @DisplayName("예약 정상 조회")
    class SuccessfulReservationSearch {

        @Test
        @DisplayName("다음 페이지 있음")
        void 다음_페이지_있음() {

            List<ReservationSearchResponse> nextPage = new ArrayList<>(List.of(SearchResponse1, SearchResponse2));

            ReservationSearchResponse res3 = new ReservationSearchResponse();
            res3.setReservationId(99L);
            nextPage.add(res3); // limit + 1 (다음페이지 있음)

            Mockito.when(reservationMapper.findAllReservationById(memberId, role, state, 100L, limit + 1)).thenReturn(nextPage);
            Mockito.when(reservationMapper.findReservationByMemberId(memberId, role)).thenReturn(10L);

            ReservationPaging result = reservationService.findAllReservationById(memberId, role, state, 100L, limit);

            assertNotNull(result);
            assertEquals(2, result.getReservations().size());    // hasSize(2)
            assertTrue(result.isHasNextPage());                          // isTrue()
            assertEquals(100L, result.getNextCursorId());        // isEqualTo(101L)

        }

        @Test
        @DisplayName("마지막 페이지 (다음 없음)")
        void 마지막_페이지_다음_없음() {
            Mockito.when(reservationMapper.findAllReservationById(memberId, role, state, -1L, limit + 1)).thenReturn(List.of(SearchResponse1, SearchResponse2));
            Mockito.when(reservationMapper.findReservationByMemberId(memberId, role)).thenReturn(2L);

            ReservationPaging result = reservationService.findAllReservationById(memberId, role, state, -1L, limit);

            assertEquals(2, result.getReservations().size());
            assertFalse(result.isHasNextPage());                        // isFalse()
            assertEquals(-1L, result.getNextCursorId());        // isEqualTo(-1L)
        }
    }

    @Nested
    @DisplayName("예약 상세 결과 없음 (예외발생)")
    class ExceptionCase {

        @Test
        @DisplayName("예약 결과 없음")
        void 예약_결과_없음() {
            Mockito.when(reservationMapper.findAllReservationById(memberId, role, state, -1L, limit + 1))
                    .thenReturn(Collections.emptyList());

            assertThrows(ReservationException.class, () ->
                    reservationService.findAllReservationById(memberId, role, state, -1L, limit));
        }
    }
}

