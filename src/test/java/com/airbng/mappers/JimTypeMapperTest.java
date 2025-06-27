package com.airbng.mappers;

import com.airbng.dto.jimType.JimTypeCountResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Tag("integration")
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {com.airbng.config.WebConfig.class})
@WebAppConfiguration
class JimTypeMapperTest {
    //  서비스 계층 - HTTP 호출과 상관 없으며 단순한 로직 검증만 하면 됨.

    @Autowired
    JimTypeMapper jimTypeMapper;

    @Nested
    @DisplayName("ReservationJimType insert 테스트")
    class InsertJimTypeTest{
        @Test
        @DisplayName("성공")
        @Transactional
        void 예약할_짐종류_추가_성공() {
            // Given : 어떠한 데이터가 주어질 때
            Long reservationId = 1L;
            List<JimTypeCountResult> jimTypeCountResults = Arrays.asList(
                    new JimTypeCountResult(1L, 2L),
                    new JimTypeCountResult(2L, 3L)
            );

            // When : 어떠한 기능을 실행하면
            int cnt = jimTypeMapper.insertReservationJimTypes(reservationId, jimTypeCountResults);

            // Then : 어떠한 결과를 기대한다
            assertEquals(2, cnt);
        }

        @Nested
        @DisplayName("실패 - 일부 필드가 null인 경우")
        class InsertJimTypeTestFailure {
            @Test
            @DisplayName("reservationId가 null인 경우")
            @Transactional
            void 예약필드_NULL() {
                // Given
                Long reservationId = null;
                List<JimTypeCountResult> jimTypeCountResults = Arrays.asList(
                        new JimTypeCountResult(1L, 2L),
                        new JimTypeCountResult(2L, 3L)
                );

                // When, Then
                assertThrows(DataIntegrityViolationException.class, () -> {
                    jimTypeMapper.insertReservationJimTypes(reservationId, jimTypeCountResults);
                });
            }

            @Test
            @DisplayName("jimtypeId가 null인 경우")
            @Transactional
            void 짐타입_NULL() {
                // Given
                Long reservationId = 1L;
                List<JimTypeCountResult> jimTypeCountResults = Arrays.asList(
                        new JimTypeCountResult(null, 2L),
                        new JimTypeCountResult(2L, 3L)
                );

                // When, Then
                assertThrows(DataIntegrityViolationException.class, () -> {
                    jimTypeMapper.insertReservationJimTypes(reservationId, jimTypeCountResults);
                });
            }
        }
    }

    @Nested
    @DisplayName("validateLockerJimTypes 테스트")
    class ValidateLockerJimTypeTest {
        @Test
        @DisplayName("성공 - 해당 보관소가 관리하는 짐 종류인 경우")
        @Transactional
        void 해당_보관소가_관리하는_짐() {
            // Given
            Long lockerId = 1L;
            List<Long> jimTypeIds = Arrays.asList(1L, 2L);

            // When
            boolean result = jimTypeMapper.validateLockerJimTypes(lockerId, jimTypeIds, jimTypeIds.size());

            // Then
            assertEquals(true, result);
        }

        @Test
        @DisplayName("실패 - 해당 보관소가 관리하지 않는 짐 종류인 경우")
        @Transactional
        void 해당_보관소가_관리하지_않는_짐() {
            // Given
            Long lockerId = 1L;
            List<Long> jimTypeIds = Arrays.asList(1L, 2L, 3L); // 3L은 해당 보관소에 존재하지 않는 짐 종류

            // When
            boolean result = jimTypeMapper.validateLockerJimTypes(lockerId, jimTypeIds, jimTypeIds.size());

            // Then
            assertEquals(false, result);
        }
    }

}