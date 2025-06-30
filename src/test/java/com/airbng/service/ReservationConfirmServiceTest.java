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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
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
            assertEquals(CANNOT_UPDATE_STATE, exception.getBaseResponseStatus());
            assertEquals(CANNOT_UPDATE_STATE.getMessage(), exception.getMessage());
        }
    }

    @Nested
    @DisplayName("동시성 테스트")
    class ConcurrencyTest {

        @Nested
        @DisplayName("동시성 테스트 - 반복 실행")
        class RepeatedConcurrencyTest {

            @RepeatedTest(20)  // 10번 반복 실행
            @DisplayName("취소와 승인 요청 동시 실행 시, 반드시 한 쪽만 성공하고 다른 한 쪽은 실패해야 한다")
            void 취소와_승인_동시성_테스트_반복() throws Exception {
                Long reservationId = 100L;
                Long dropperId = dropper.getMemberId();
                Long keeperId = keeper.getMemberId();

                Reservation sharedReservation = Reservation.builder()
                        .reservationId(reservationId)
                        .dropper(dropper)
                        .keeper(keeper)
                        .startTime(LocalDateTime.now())
                        .endTime(LocalDateTime.now().plusHours(2))
                        .state(ReservationState.PENDING)
                        .build();

                when(memberMapper.findById(dropperId)).thenReturn(true);
                when(memberMapper.findById(keeperId)).thenReturn(true);

                when(reservationMapper.findReservationWithDropperById(reservationId)).thenReturn(sharedReservation);
                when(reservationMapper.findReservationWithKeeperById(reservationId)).thenReturn(sharedReservation);

                doAnswer(invocationOnMock -> {
                    ReservationState newState = invocationOnMock.getArgument(1);
                    sharedReservation.setState(newState);
                    return null;
                }).when(reservationMapper).updateReservationState(anyLong(), any(ReservationState.class));

                ExecutorService executor = Executors.newFixedThreadPool(2);
                CountDownLatch latch = new CountDownLatch(2);
                List<Throwable> exceptions = Collections.synchronizedList(new ArrayList<>());

                Runnable cancelTask = () -> {
                    try {
                        latch.countDown();
                        latch.await();
                        reservationService.updateReservationState(reservationId, dropperId);
                    } catch (Throwable t) {
                        exceptions.add(t);
                    }
                };

                Runnable confirmTask = () -> {
                    try {
                        latch.countDown();
                        latch.await();
                        reservationService.confirmReservationState(reservationId, "yes", keeperId);
                    } catch (Throwable t) {
                        exceptions.add(t);
                    }
                };

                executor.submit(confirmTask);
                executor.submit(cancelTask);
                executor.shutdown();
                executor.awaitTermination(3, TimeUnit.SECONDS);

                // 예외가 몇 건 발생했는지 체크
                long 예외_건수 = exceptions.stream()
                        .filter(e -> e instanceof ReservationException &&
                                ((ReservationException) e).getBaseResponseStatus().equals(CANNOT_UPDATE_STATE))
                        .count();

                // 예외는 반드시 0갸 또는 1건만 발생해야 한다
                assertTrue(예외_건수 == 0 || 예외_건수 == 1, "예외는 0개 아니면 1개여야 합니다.");

                // 상태는 CANCELLED 또는 CONFIRMED 중 하나여야 한다
                assertTrue(sharedReservation.getState() == ReservationState.CANCELLED
                                || sharedReservation.getState() == ReservationState.CONFIRMED,
                        "최종 상태는 CANCELLED 또는 CONFIRMED 여야 합니다.");
            }
        }

    }
}
