package com.airbng.service;

import com.airbng.common.exception.ReservationException;
import com.airbng.domain.Member;
import com.airbng.domain.Reservation;
import com.airbng.domain.base.BaseStatus;
import com.airbng.domain.base.ReservationState;
import com.airbng.domain.image.Image;
import com.airbng.domain.jimtype.JimType;
import com.airbng.domain.jimtype.ReservationJimType;
import com.airbng.dto.reservation.ReservationDetailResponse;
import com.airbng.mappers.MemberMapper;
import com.airbng.mappers.ReservationMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static com.airbng.common.response.status.BaseResponseStatus.NOT_DROPPER_OF_RESERVATION;
import static com.airbng.common.response.status.BaseResponseStatus.NOT_FOUND_RESERVATION;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ReservationDetailTest {

    @Mock
    private ReservationMapper reservationMapper;

    @Mock
    private MemberMapper memberMapper;
    @InjectMocks
    private ReservationServiceImpl reservationService;

    Member dropper, keeper, 누구세요;

    JimType 백팩, 캐리어, 초대형캐리어;

    Reservation reservation;

    @BeforeEach
    public void setUp() {
        Image image = Image.builder()
                .imageId(1L)
                .url("https://example.com/images/profile")
                .uploadName("profile")
                .build();

        dropper = Member.builder()
                .memberId(1L)
                .email("test@airbng.com")
                .name("테스터")
                .phone("010-0000-0000")
                .nickname("testUser")
                .password("encoded_test_password")
                .status(BaseStatus.ACTIVE)
                .profileImage(image)
                .build();

        keeper = Member.builder()
                .memberId(2L)
                .email("test@airbng.com")
                .name("테스터")
                .phone("010-0000-0000")
                .nickname("testUser")
                .password("encoded_test_password")
                .status(BaseStatus.ACTIVE)
                .profileImage(image)
                .build();

        누구세요 = Member.builder()
                .memberId(3L)
                .email("test2@airbng.com")
                .name("누구세요")
                .phone("010-0000-0000")
                .nickname("testUser")
                .password("encoded_test_password")
                .status(BaseStatus.ACTIVE)
                .profileImage(image)
                .build();

        reservation = Reservation.builder()
                .reservationId(1L)
                .dropper(dropper)
                .keeper(keeper)
                .startTime(LocalDateTime.now().plusDays(1))
                .endTime(LocalDateTime.now().plusDays(2))
                .state(ReservationState.PENDING)
                .build();

        백팩 = JimType.builder()
                .jimTypeId(1L)
                .typeName("백팩")
                .pricePerHour(2000L)
                .build();

        캐리어 = JimType.builder()
                .jimTypeId(2L)
                .typeName("캐리어")
                .pricePerHour(2000L)
                .build();

        초대형캐리어 = JimType.builder()
                .jimTypeId(3L)
                .typeName("초대형 캐리어")
                .pricePerHour(6000L)
                .build();

        reservation.setReservationJimTypes(List.of(
                new ReservationJimType(1L, 백팩, reservation,1L),
                new ReservationJimType(2L, 캐리어, reservation,1L)
        ));

    }

    @Nested
    @DisplayName("예약_상세_조회_성공_테스트")
    class ReservationDetailTestSuccess{
        @Test
        @DisplayName("예약_상세_조회_성공")
        void 에약_상세_조회_성공() {
            //given
            Long reservationId = 1L;
            when(reservationMapper.findReservationDetailById(reservationId)).thenReturn(reservation);

            // when
            ReservationDetailResponse response = reservationService.findReservationDetail(reservationId,dropper.getMemberId());

            // then
            assertEquals(reservationId, response.getReservationId());
        }
    }

    @Nested
    @DisplayName("에약_상세_조회_실패_테스트")
    class ReservationDetailTestFailure{
        @Test
        @DisplayName("해당_예약의_드롭퍼가_아닐때")
        void 해당_예약의_드롭퍼가_아님() {
            //given
            Long reservationId = 1L;

            when(reservationMapper.findReservationDetailById(reservationId)).thenReturn(reservation);

            // when
            ReservationException exception = assertThrows(ReservationException.class, ()
                    ->reservationService.findReservationDetail(reservationId,누구세요.getMemberId()));

            // then
            assertEquals(NOT_DROPPER_OF_RESERVATION,exception.getBaseResponseStatus());
            assertEquals(NOT_DROPPER_OF_RESERVATION.getMessage(),exception.getMessage());
        }

        @Test
        @DisplayName("해당_예약이_없을때")
        void 해당_예약이_없음() {
            //given
            Long reservationId = 3L;

            // when
            ReservationException exception = assertThrows(ReservationException.class, ()
                    ->reservationService.findReservationDetail(reservationId,dropper.getMemberId()));

            // then
            assertEquals(NOT_FOUND_RESERVATION,exception.getBaseResponseStatus());
            assertEquals(NOT_FOUND_RESERVATION.getMessage(),exception.getMessage());
        }

    }
}

