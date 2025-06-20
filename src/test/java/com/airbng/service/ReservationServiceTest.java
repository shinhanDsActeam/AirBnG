package com.airbng.service;

import com.airbng.common.exception.JimTypeException;
import com.airbng.common.exception.LockerException;
import com.airbng.common.exception.MemberException;
import com.airbng.common.exception.ReservationException;
import com.airbng.common.response.status.BaseResponseStatus;
import com.airbng.domain.Locker;
import com.airbng.domain.Member;
import com.airbng.domain.base.Available;
import com.airbng.domain.base.BaseStatus;
import com.airbng.domain.image.Image;
import com.airbng.domain.image.LockerImage;
import com.airbng.domain.jimtype.JimType;
import com.airbng.domain.jimtype.LockerJimType;
import com.airbng.dto.jimType.JimTypeCountResult;
import com.airbng.dto.reservation.ReservationInsertRequest;
import com.airbng.mappers.JimTypeMapper;
import com.airbng.mappers.LockerMapper;
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

import java.util.Arrays;
import java.util.List;

import static com.airbng.common.response.status.BaseResponseStatus.CREATED_RESERVATION;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @Mock
    private ReservationMapper reservationMapper;
    @Mock
    private MemberMapper memberMapper;
    @Mock
    private JimTypeMapper jimTypeMapper;
    @Mock
    private LockerMapper lockerMapper;

    @InjectMocks
    private ReservationServiceImpl reservationService;

    private Locker 서울역_보관소;
    private Member 보관왕;
    JimType 백팩;
    JimType 캐리어;
    JimType 초대형캐리어;

    private Member 맡김왕;
    private ReservationInsertRequest perfectRequest;

    @BeforeEach
    public void setUp() {
        보관왕 = Member.builder()
                .memberId(1L)
                .email("keeper1@airbng.com")
                .name("강보관")
                .phone("01011112222")
                .nickname("보과니")
                .password("pw1234")
                .status(BaseStatus.ACTIVE)
                .profileImage(new Image(1L, "profile1.jpg", "https://s3.amazonaws.com/airbng/profile.jpg"))
                .build();

        맡김왕 = Member.builder()
                .memberId(2L)
                .email("keeper2@airbng.com")
                .name("박맡김")
                .phone("01022223333")
                .nickname("맡기미")
                .password("pw1234")
                .status(BaseStatus.ACTIVE)
                .profileImage(new Image(2L, "profile2.jpg", "https://s3.amazonaws.com/airbng/profile.jpg"))
                .build();


        서울역_보관소 = Locker.builder()
                .lockerId(1L)
                .lockerName("서울역 보관소")
                .isAvailable(Available.YES)
                .address("서울 중구 세종대로 1")
                .addressEnglish("1, Sejong-daero, Jung-gu, Seoul")
                .addressDetail("1층")
                .latitude(35.127197)
                .longitude(126.912106)
                .keeper(보관왕)
                .lockerImages(null)
                .build();

        서울역_보관소.setLockerImages(List.of(
                new LockerImage(
                        1L, 서울역_보관소,
                        new Image(3L, "locker2.jpg", "https://s3.amazonaws.com/airbng/locker2.jpg")
                ),
                new LockerImage(
                        2L, 서울역_보관소,
                        new Image(4L, "locker3.jpg", "https://s3.amazonaws.com/airbng/locker3.jpg")
                )
        ));

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


        서울역_보관소.setLockerJimTypes(List.of(
                new LockerJimType(1L, 백팩, 서울역_보관소),
                new LockerJimType(2L, 캐리어, 서울역_보관소)
        ));

        perfectRequest = ReservationInsertRequest.builder()
                .lockerId(서울역_보관소.getLockerId())
                .keeperId(보관왕.getMemberId())
                .dropperId(맡김왕.getMemberId())
                .startTime("2025-10-02 10:00:00")
                .endTime("2025-10-02 17:00:00")
                .jimTypeCounts(List.of(
                        new JimTypeCountResult(백팩.getJimTypeId(), 1L),
                        new JimTypeCountResult(캐리어.getJimTypeId(), 3L)
                ))
                .build();
    }

    @Nested
    @DisplayName("예약 등록 테스트")
    class InsertReservationTest {

        @Test
        @DisplayName("예약 등록 성공")
        void 예약_등록_성공() throws LockerException, MemberException {
            // given
            ReservationInsertRequest request = perfectRequest;

            // when
            when(memberMapper.isExistMember(보관왕.getMemberId())).thenReturn(true);
            when(memberMapper.isExistMember(맡김왕.getMemberId())).thenReturn(true);
            when(lockerMapper.isExistLocker(서울역_보관소.getLockerId())).thenReturn(true);
            when(lockerMapper.isLockerKeeper(서울역_보관소.getLockerId(), 보관왕.getMemberId())).thenReturn(true);
            when(jimTypeMapper.validateLockerJimTypes(서울역_보관소.getLockerId(), List.of(백팩.getJimTypeId(), 캐리어.getJimTypeId()), 2)).thenReturn(true);

            when(reservationMapper.selectLastInsertId()).thenReturn(1L); // 예약 삽입 성공 시 1L 아이디 리턴
            when(jimTypeMapper.insertReservationJimTypes(1L, request.getJimTypeCounts())).thenReturn(request.getJimTypeCounts().size());

            BaseResponseStatus status = reservationService.insertReservation(request);

            // then
//            assertEquals(request.getJimTypeCounts().size(), 2);
            assertEquals(CREATED_RESERVATION, status);
        }

        @Nested
        @DisplayName("실패")
        class InsertReservationTestFailure {


            @Test
            @DisplayName("존재하지 않는 보관소에 예약을 시도할 때")
            void 존재하지_않는_보관소() {
                // given
                ReservationInsertRequest request = perfectRequest;
                request.setLockerId(999L); // 존재하지 않는 보관소 ID로 변경

                // when
                when(memberMapper.isExistMember(보관왕.getMemberId())).thenReturn(true);
                when(memberMapper.isExistMember(맡김왕.getMemberId())).thenReturn(true);

                when(lockerMapper.isExistLocker(request.getLockerId())).thenReturn(false);
                LockerException exception = assertThrows(LockerException.class, () -> {
                    reservationService.insertReservation(request);
                });

                // then
                assertEquals(BaseResponseStatus.NOT_FOUND_LOCKER, exception.getBaseResponseStatus());
            }

            @Nested
            @DisplayName("회원 검증 실패")
            class ValidateMemberFailure {
                @Test
                @DisplayName("존재하지 않는 회원으로 예약을 시도할 때")
                void 존재하지_않는_회원() {
                    // given
                    ReservationInsertRequest request = perfectRequest;
                    request.setKeeperId(999L); // 존재하지 않는 회원 ID로 변경

                    // when
                    when(memberMapper.isExistMember(맡김왕.getMemberId())).thenReturn(true);
                    when(memberMapper.isExistMember(999L)).thenReturn(false);

                    MemberException exception = assertThrows(MemberException.class, () -> {
                        reservationService.insertReservation(request);
                    });

                    // then
                    assertEquals(BaseResponseStatus.NOT_FOUND_MEMBER, exception.getBaseResponseStatus());
                }

                @Test
                @DisplayName("dropper와 keeper가 동일할 때")
                void 맡길사람과_맡겨줄사람이_동일한_경우() {
                    // given
                    ReservationInsertRequest request = perfectRequest;
                    request.setKeeperId(맡김왕.getMemberId()); // dropper와 keeper를 동일하게 설정

                    // when
                    when(memberMapper.isExistMember(anyLong())).thenReturn(true);

                    ReservationException exception = assertThrows(ReservationException.class, () -> {
                        reservationService.insertReservation(request);
                    });

                    // then
                    assertEquals(BaseResponseStatus.INVALID_RESERVATION_PARTICIPANTS, exception.getBaseResponseStatus());
                }
            }

            @Nested
            @DisplayName("짐타입 검증 실패")
            class ValidateJimTypesFailure {

                @BeforeEach
                void setUp() {
                    when(memberMapper.isExistMember(보관왕.getMemberId())).thenReturn(true);
                    when(memberMapper.isExistMember(맡김왕.getMemberId())).thenReturn(true);
                    when(lockerMapper.isExistLocker(서울역_보관소.getLockerId())).thenReturn(true);
                    when(lockerMapper.isLockerKeeper(서울역_보관소.getLockerId(), 보관왕.getMemberId())).thenReturn(true);
                    when(reservationMapper.selectLastInsertId()).thenReturn(1L); // 예약 삽입 성공 시 1L 아이디 리턴
                }

                @Test
                @DisplayName("존재하지 않는 짐타입에 예약을 시도할 때")
                void 존재하지_않는_짐타입() {
                    // given
                    ReservationInsertRequest request = perfectRequest;
                    request.setJimTypeCounts(Arrays.asList(
                            new JimTypeCountResult(백팩.getJimTypeId(), 1L),
                            new JimTypeCountResult(캐리어.getJimTypeId(), 3L),
                            new JimTypeCountResult(999L, 1L) // 존재하지 않는 짐타입 ID 추가
                    ));

                    // when
                    when(jimTypeMapper.validateLockerJimTypes(서울역_보관소.getLockerId(), List.of(백팩.getJimTypeId(), 캐리어.getJimTypeId(), 999L), 3))
                            .thenReturn(false);

                    JimTypeException exception = assertThrows(JimTypeException.class, () -> {
                        reservationService.insertReservation(request);
                    });

                    // then
                    assertEquals(BaseResponseStatus.LOCKER_DOES_NOT_SUPPORT_JIMTYPE, exception.getBaseResponseStatus());
                }

                @Test
                @DisplayName("보관소가 관리하지 않는 짐타입에 예약을 시도할 때")
                void 보관소가_관리하지_않는_짐타입() {
                    // given
                    ReservationInsertRequest request = perfectRequest;
                    request.setJimTypeCounts(Arrays.asList(
                            new JimTypeCountResult(백팩.getJimTypeId(), 1L),
                            new JimTypeCountResult(캐리어.getJimTypeId(), 3L),
                            new JimTypeCountResult(초대형캐리어.getJimTypeId(), 1L) // 존재하지 않는 짐타입 ID 추가
                    ));
                    // when
                    when(jimTypeMapper.validateLockerJimTypes(서울역_보관소.getLockerId(), List.of(백팩.getJimTypeId(), 캐리어.getJimTypeId(), 초대형캐리어.getJimTypeId()), 3))
                            .thenReturn(false);

                    JimTypeException exception = assertThrows(JimTypeException.class, () -> {
                        reservationService.insertReservation(request);
                    });

                    // then
                    assertEquals(BaseResponseStatus.LOCKER_DOES_NOT_SUPPORT_JIMTYPE, exception.getBaseResponseStatus());
                }

                @Test
                @DisplayName("요청한 짐 타입 개수와 실제 등록된 개수가 일치하지 않을 때")
                void 짐타임_등록_실패() {
                    // given
                    ReservationInsertRequest request = perfectRequest;

                    // when
                    when(jimTypeMapper.validateLockerJimTypes(서울역_보관소.getLockerId(), List.of(백팩.getJimTypeId(), 캐리어.getJimTypeId()), 2))
                            .thenReturn(true);

                    when(jimTypeMapper.insertReservationJimTypes(1L, request.getJimTypeCounts())).thenReturn(0); // 등록 실패시 0 리턴

                    ReservationException exception = assertThrows(ReservationException.class, () -> {
                        reservationService.insertReservation(request);
                    });

                    // then
                    assertEquals(BaseResponseStatus.INVALID_JIMTYPE_COUNT, exception.getBaseResponseStatus());
                }
            }
        }

    }
}