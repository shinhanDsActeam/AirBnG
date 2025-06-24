package com.airbng.service;

import com.airbng.common.exception.LockerException;
import com.airbng.common.exception.MemberException;
import com.airbng.common.exception.ReservationException;
import com.airbng.domain.Member;
import com.airbng.domain.Reservation;
import com.airbng.domain.base.BaseStatus;
import com.airbng.domain.base.ReservationState;
import com.airbng.domain.image.Image;
import com.airbng.dto.ReservationCancelResponse;
import com.airbng.mappers.MemberMapper;
import com.airbng.mappers.ReservationMapper;
import com.github.benmanes.caffeine.cache.Cache;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import com.github.benmanes.caffeine.cache.*;
import java.util.concurrent.TimeUnit;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;

import static com.airbng.common.response.status.BaseResponseStatus.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationCancelServiceTest {

    @Mock
    private ReservationMapper reservationMapper;

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
        @DisplayName("정상 케이스 1 - 예약 대기 상태를 취소로 변경")
        void 대기중_예약_상태_취소_성공() {
            // given
            Long reservationId = 1L;
            Long memberId = 1L;

            Reservation reservation = Reservation.builder()
                    .reservationId(reservationId)
                    .dropper(dropper)
                    .keeper(keeper)
                    .startTime(LocalDateTime.now().plusDays(1))
                    .endTime(LocalDateTime.now().plusDays(2))
                    .state(ReservationState.PENDING)
                    .build();

            when(reservationMapper.findReservationWithDropperById(reservationId)).thenReturn(reservation);
            when(memberMapper.findById(memberId)).thenReturn(true);

            // when
            ReservationCancelResponse response = reservationService.updateReservationState(reservationId, memberId);

            // then
            assertEquals(reservationId, response.getReservationId());
            assertEquals(ReservationState.CANCELLED, response.getState());
            verify(reservationMapper, times(1)).updateReservationState(reservationId, ReservationState.CANCELLED);
        }

        @Test
        @DisplayName("정상 케이스 2 - 예약 완료 상태를 취소로 변경")
        void 완료_예약_상태_취소_성공() {
            // given
            Long reservationId = 2L;
            Long memberId = 1L;

            Reservation reservation = Reservation.builder()
                    .reservationId(reservationId)
                    .dropper(dropper)
                    .keeper(keeper)
                    .startTime(LocalDateTime.now().plusDays(1))
                    .endTime(LocalDateTime.now().plusDays(2))
                    .state(ReservationState.CONFIRMED)
                    .build();

            when(reservationMapper.findReservationWithDropperById(reservationId)).thenReturn(reservation);
            when(memberMapper.findById(memberId)).thenReturn(true);

            // when
            ReservationCancelResponse response = reservationService.updateReservationState(reservationId, memberId);

            // then
            assertEquals(reservationId, response.getReservationId());
            assertEquals(ReservationState.CANCELLED, response.getState());
            verify(reservationMapper, times(1)).updateReservationState(reservationId, ReservationState.CANCELLED);
        }
    }

    @Nested
    @DisplayName("예외 처리 테스트")
    class ExceptionTest {

        @Test
        @DisplayName("존재하지 않는 예약")
        void 존재하지_않는_예약() {
            // given
            Long reservationId = 999L;
            Long memberId = 1L;

            when(reservationMapper.findReservationWithDropperById(reservationId)).thenReturn(null);
            when(memberMapper.findById(memberId)).thenReturn(true);

            // when
            ReservationException exception = assertThrows(ReservationException.class, () ->
                    reservationService.updateReservationState(reservationId, memberId)
            );

            // then
            assertEquals(NOT_FOUND_RESERVATION, exception.getBaseResponseStatus());
            assertEquals(NOT_FOUND_RESERVATION.getMessage(), exception.getMessage());

        }

        @Test
        @DisplayName("예약 상태가 이미 취소됨 (변경 불가)")
        void 예약_상태가_취소됨() {
            // given
            Long reservationId = 3L;
            Long memberId = 1L;

            Reservation reservation = Reservation.builder()
                    .reservationId(reservationId)
                    .dropper(dropper)
                    .keeper(keeper)
                    .startTime(LocalDateTime.now().plusDays(1))
                    .endTime(LocalDateTime.now().plusDays(2))
                    .state(ReservationState.CANCELLED)
                    .build();

            when(reservationMapper.findReservationWithDropperById(reservationId)).thenReturn(reservation);
            when(memberMapper.findById(memberId)).thenReturn(true);

            // when
            ReservationException exception = assertThrows(ReservationException.class, () ->
                    reservationService.updateReservationState(reservationId, memberId)
            );

            // then
            assertEquals(CANNOT_UPDATE_STATE, exception.getBaseResponseStatus());
            assertEquals(CANNOT_UPDATE_STATE.getMessage(), exception.getMessage());
        }

        @Test
        @DisplayName("예약 상태가 이미 완료됨 (변경 불가)")
        void 예약_상태가_완료됨() {
            // given
            Long reservationId = 4L;
            Long memberId = 1L;

            Reservation reservation = Reservation.builder()
                    .reservationId(reservationId)
                    .dropper(dropper)
                    .keeper(keeper)
                    .startTime(LocalDateTime.now().plusDays(1))
                    .endTime(LocalDateTime.now().plusDays(2))
                    .state(ReservationState.COMPLETED)
                    .build();

            when(reservationMapper.findReservationWithDropperById(reservationId)).thenReturn(reservation);
            when(memberMapper.findById(memberId)).thenReturn(true);

            // when
            ReservationException exception = assertThrows(ReservationException.class, () ->
                    reservationService.updateReservationState(reservationId, memberId)
            );

            // then
            assertEquals(CANNOT_UPDATE_STATE, exception.getBaseResponseStatus());
            assertEquals(CANNOT_UPDATE_STATE.getMessage(), exception.getMessage());
        }

        @Test
        @DisplayName("요청한 맴버가 없을때 예외 발생")
        @MockitoSettings(strictness = Strictness.LENIENT)
        void 맴버_없음_예외() {
            // given
            Long reservationId = 5L;
            Long memberId = 999L; // 존재하지 않는 멤버

            Reservation reservation = Reservation.builder()
                    .reservationId(reservationId)
                    .dropper(dropper)
                    .keeper(keeper)
                    .startTime(LocalDateTime.now().plusDays(1))
                    .endTime(LocalDateTime.now().plusDays(2))
                    .state(ReservationState.PENDING)
                    .build();

            when(reservationMapper.findReservationWithDropperById(reservationId)).thenReturn(reservation);
            when(memberMapper.findById(memberId)).thenReturn(false); // 맴버가 없다면

            // when
            MemberException exception = assertThrows(MemberException.class, () ->
                    reservationService.updateReservationState(reservationId, memberId)
            );

            // then
            assertEquals(NOT_FOUND_MEMBER, exception.getBaseResponseStatus());
            assertEquals(NOT_FOUND_MEMBER.getMessage(), exception.getMessage());
        }

        @Test
        @DisplayName("예약자와 요청자가 불일치 시 예외 발생")
        void 예약자_불일치_예외() {
            // given
            Long reservationId = 6L;
            Long memberId = 999L; // dropper 와 다른 memberId

            Reservation reservation = Reservation.builder()
                    .reservationId(reservationId)
                    .dropper(dropper) // memberId = 1L
                    .keeper(keeper)
                    .startTime(LocalDateTime.now().plusDays(1))
                    .endTime(LocalDateTime.now().plusDays(2))
                    .state(ReservationState.PENDING)
                    .build();

            when(reservationMapper.findReservationWithDropperById(reservationId)).thenReturn(reservation);
            when(memberMapper.findById(memberId)).thenReturn(true); //맴버는 있음

            // when
            ReservationException exception = assertThrows(ReservationException.class, () ->
                    reservationService.updateReservationState(reservationId, memberId)
            );

            // then
            assertEquals(NOT_DROPPER_OF_RESERVATION, exception.getBaseResponseStatus());
            assertEquals(NOT_DROPPER_OF_RESERVATION.getMessage(), exception.getMessage());
        }
    }

    @Nested
    @DisplayName("카페인 캐시 만료 테스트")
    class CaffeineExpireAfterWriteTest {

        @Test
        void 쓰기_이후_만료_테스트() {
            AtomicLong  fakeNanos = new AtomicLong();

            // Cache 생성
            Cache<String, String> cache = Caffeine.newBuilder()
                    .expireAfterWrite(5, TimeUnit.MINUTES)
                    .scheduler(Scheduler.systemScheduler()) // ✅ scheduler 만 사용
                    .recordStats()
                    .build();

            // put
            cache.put("key1", "value1");

            // 0초 → 존재함
            assertEquals(cache.getIfPresent("key1"),"value1");

            // 3분 경과
            fakeNanos.addAndGet(TimeUnit.MINUTES.toNanos(3));
            assertEquals(cache.getIfPresent("key1"),"value1");
            // 5분 추가 경과 (총 8분)
            fakeNanos.addAndGet(TimeUnit.MINUTES.toNanos(5));

            cache.cleanUp();

            assertEquals(cache.getIfPresent("key1"),null);
            System.out.println(cache.stats());

        }
    }

}
