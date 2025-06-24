package com.airbng.service;

import com.airbng.dto.jimType.JimTypeCountResult;
import com.airbng.dto.reservation.ReservationInsertRequest;
import com.airbng.mappers.JimTypeMapper;
import com.airbng.mappers.ReservationMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {com.airbng.config.WebConfig.class})
@WebAppConfiguration
class ReservationMapperTest {
    //  서비스 계층 - HTTP 호출과 상관 없으며 단순한 로직 검증만 하면 됨.

    @Autowired
    ReservationMapper reservationMapper;

    @Nested
    @DisplayName("Reservation insert 테스트")
    class InsertReservationTest{
        @Test
        @DisplayName("성공")
        @Transactional
        void 예약_2개_추가_성공() {
            // Given
            ReservationInsertRequest reservation1 = ReservationInsertRequest.builder()
                    .dropperId(1L)
                    .keeperId(2L)
                    .lockerId(3L)
                    .startTime("2025-07-01 10:00:00")
                    .endTime("2025-07-01 12:00:00")
                    .jimTypeCounts(Arrays.asList(
                            new JimTypeCountResult(1L, 2L), // 짐 타입 ID와 개수
                            new JimTypeCountResult(2L, 3L)
                    ))
                    .build();

            ReservationInsertRequest reservation2 = ReservationInsertRequest.builder()
                    .dropperId(1L)
                    .keeperId(2L)
                    .lockerId(3L)
                    .startTime("2025-07-02 10:00:00")
                    .endTime("2025-07-02 12:00:00")
                    .jimTypeCounts(Arrays.asList(
                            new JimTypeCountResult(1L, 2L), // 짐 타입 ID와 개수
                            new JimTypeCountResult(2L, 3L)
                    ))
                    .build();

            // When
            reservationMapper.insertReservation(reservation1);
            Long reservationId1 = 1L;
            reservationMapper.insertReservation(reservation2);
            Long reservationId2 = 2L;

            // Then
            assertTrue(reservationId1 > 0);
            assertTrue(reservationId2 > 0);
            assertNotEquals(reservationId1, reservationId2);
        }

        @Test
        @DisplayName("실패")
        @Transactional
        void 예약_추가_실패() {
            // Given
            ReservationInsertRequest reservation = ReservationInsertRequest.builder()
                    .dropperId(null) // dropperId가 null인 경우
                    .keeperId(2L)
                    .lockerId(3L)
                    .startTime("2025-07-01 10:00:00")
                    .endTime("2025-07-01 12:00:00")
                    .jimTypeCounts(Arrays.asList(
                            new JimTypeCountResult(1L, 2L),
                            new JimTypeCountResult(2L, 3L)
                    ))
                    .build();

            // When & Then
            assertThrows(DataIntegrityViolationException.class, () -> {
                reservationMapper.insertReservation(reservation);
            });
        }
    }

}