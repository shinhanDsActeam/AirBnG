package com.airbng.service;

import com.airbng.common.exception.ReservationException;
import com.airbng.domain.Member;
import com.airbng.domain.Reservation;
import com.airbng.domain.base.BaseStatus;
import com.airbng.domain.base.ReservationState;
import com.airbng.domain.image.Image;
import com.airbng.dto.reservation.ReservationConfirmResponse;
import com.airbng.mappers.MemberMapper;
import com.airbng.mappers.ReservationMapper;
import com.github.benmanes.caffeine.cache.Cache;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.ReentrantLock;

import static com.airbng.common.response.status.BaseResponseStatus.ALREADY_CANCELLED_RESERVATION;
import static com.airbng.common.response.status.BaseResponseStatus.CANNOT_UPDATE_STATE;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReservationConfirmServiceTest {

    @Mock
    private ReservationMapper  reservationMapper;
    @Mock
    private MemberMapper memberMapper;

    @Mock
    private Cache<Long, ReentrantLock> reservationLocks;

    Member dropper, keeper;

    @InjectMocks
    private ReservationServiceImpl reservationService;

    @BeforeEach
    void setUp() {
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

        when(reservationLocks.get(anyLong(), any())).thenAnswer(invocation -> new ReentrantLock());
    }

    @Nested
    @DisplayName("요청 성공 테스트")
    class SuccessTest {
        @Test
        @DisplayName("정상 케이스 - 예약상태를 CONFIRMED 변경")
        void 예약_승인_성공() {

            //given
            Long reservationId = 1L;
            Long memberId = keeper.getMemberId();
            String approve = "yes";

            Reservation reservation = Reservation.builder()
                    .reservationId(reservationId)
                    .dropper(dropper)
                    .keeper(keeper)
                    .startTime(LocalDateTime.now())
                    .endTime(LocalDateTime.now().plusDays(1))
                    .state(ReservationState.PENDING)
                    .build();
            when(memberMapper.findById(memberId)).thenReturn(true);
            when(reservationMapper.findReservationWithKeeperById(reservationId)).thenReturn(reservation);

            //when
            ReservationConfirmResponse response = reservationService.confirmReservationState(reservationId, approve, memberId);

            //then
            assertEquals(ReservationState.CONFIRMED, response.getState());
            verify(reservationMapper).updateReservationState(reservationId, ReservationState.CONFIRMED);
        }

        @Test
        @DisplayName("정상 케이스 - 예약상태를 CANCELED 변경")
        void 예약_거절_성공() {
            //given
            Long reservationId = 2L;
            Long memberId = keeper.getMemberId();
            String approve = "no";

            Reservation reservation = Reservation.builder()
                    .reservationId(reservationId)
                    .dropper(dropper)
                    .keeper(keeper)
                    .startTime(LocalDateTime.now())
                    .endTime(LocalDateTime.now().plusDays(1))
                    .state(ReservationState.PENDING)
                    .build();
            when(memberMapper.findById(memberId)).thenReturn(true);
            when(reservationMapper.findReservationWithKeeperById(reservationId)).thenReturn(reservation);

            //when
            ReservationConfirmResponse response = reservationService.confirmReservationState(reservationId, approve, memberId);

            //then
            assertEquals(ReservationState.CANCELLED, response.getState());
            verify(reservationMapper).updateReservationState(reservationId, ReservationState.CANCELLED);
        }
    }

    @Nested
    @DisplayName("예외 처리 테스트")
    class ExceptionTest {

        @Test
        @DisplayName("예약 상태가 이미 취소됨 (변경 불가)")
        void 예약_상태가_완료됨() {
            //given
            Long reservationId = 3L;
            Long memberId = keeper.getMemberId();
            String approve = "yes";

            Reservation reservation = Reservation.builder()
                    .reservationId(reservationId)
                    .dropper(dropper)
                    .keeper(keeper)
                    .startTime(LocalDateTime.now())
                    .endTime(LocalDateTime.now().plusDays(1))
                    .state(ReservationState.CANCELLED)
                    .build();
            when(memberMapper.findById(memberId)).thenReturn(true);
            when(reservationMapper.findReservationWithKeeperById(reservationId)).thenReturn(reservation);

            //when
            ReservationException exception = assertThrows(ReservationException.class, () ->
                    reservationService.confirmReservationState(reservationId, approve, memberId));

            //then
            assertEquals(ALREADY_CANCELLED_RESERVATION, exception.getBaseResponseStatus());
            assertEquals(ALREADY_CANCELLED_RESERVATION.getMessage(), exception.getMessage());
        }
    }
}
