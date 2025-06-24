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
    private static final Long LIMIT = 10L;

    @InjectMocks
    private ReservationServiceImpl reservationService;

    // 다음 페이지가 있는 경우를 테스트하기 위해 11개 예약을 생성
    List<ReservationSearchResponse> nextPage = new ArrayList<>();

    @BeforeEach
    void setup() {
        // LIMIT이 10이므로 limit+1인 10개 만들어야 함
        for (long i = 109L; i >= 100L; i--) {
            ReservationSearchResponse res = new ReservationSearchResponse();
            res.setReservationId(i);
            nextPage.add(res);
        }
    }

    @Nested
    @DisplayName("예약 정상 조회")
    class SuccessfulReservationSearch {

        @Test
        @DisplayName("다음 페이지 있음")
        void 다음_페이지_있음() {

            ReservationSearchResponse res3 = new ReservationSearchResponse();
            res3.setReservationId(99L);
            nextPage.add(res3); // limit + 1 (다음페이지 있음)

            Mockito.when(reservationMapper.findAllReservationById(memberId, role, state, 100L, LIMIT + 1)).thenReturn(nextPage);
            Mockito.when(reservationMapper.findReservationByMemberId(memberId, role)).thenReturn(999L);

            ReservationPaging result = reservationService.findAllReservationById(memberId, role, state, 100L);

            assertNotNull(result);
            assertEquals(10, result.getReservations().size());    // hasSize(10)
            assertTrue(result.isHasNextPage());                          // isTrue()
            assertEquals(100L, result.getNextCursorId());        // isEqualTo(100L)

        }

        @Test
        @DisplayName("마지막 페이지 (다음 없음)")
        void 마지막_페이지_다음_없음() {
            Mockito.when(reservationMapper.findAllReservationById(memberId, role, state, -1L, LIMIT + 1)).thenReturn(nextPage);
            Mockito.when(reservationMapper.findReservationByMemberId(memberId, role)).thenReturn(999L);

            ReservationPaging result = reservationService.findAllReservationById(memberId, role, state, -1L);

            assertEquals(10, result.getReservations().size());
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
            Mockito.when(reservationMapper.findAllReservationById(memberId, role, state, -1L, LIMIT + 1))
                    .thenReturn(Collections.emptyList());

            assertThrows(ReservationException.class, () ->
                    reservationService.findAllReservationById(memberId, role, state, -1L));
        }
    }
}

