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
import com.airbng.dto.jimType.LockerJimTypeResult;
import com.airbng.dto.reservation.ReservationFormResponse;
import com.airbng.dto.reservation.ReservationInsertRequest;
import com.airbng.dto.reservation.ReservationInsertResponse;
import com.airbng.mappers.JimTypeMapper;
import com.airbng.mappers.LockerMapper;
import com.airbng.mappers.ReservationMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpSession;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.airbng.common.response.status.BaseResponseStatus.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {
    @Mock
    private ReservationMapper reservationMapper;
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
        MockHttpSession session = new MockHttpSession();

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

        session.setAttribute("memberId", 맡김왕.getMemberId());

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
            ReservationInsertRequest request = Mockito.spy(perfectRequest);

            // when
            when(lockerMapper.isExistLocker(서울역_보관소.getLockerId())).thenReturn(true);
            when(lockerMapper.getLockerKeeperId(서울역_보관소.getLockerId())).thenReturn(보관왕.getMemberId());
            when(jimTypeMapper.validateLockerJimTypes(서울역_보관소.getLockerId(), List.of(백팩.getJimTypeId(), 캐리어.getJimTypeId()), 2)).thenReturn(true);
            Long insertedId = 1L; // stubbing
            when(request.getId()).thenReturn(insertedId); // 예약 삽입 성공 시 id set 됨
            List<JimTypeCountResult> jimTypeCounts = request.getJimTypeCounts(); // stubbing
            when(jimTypeMapper.insertReservationJimTypes(insertedId, jimTypeCounts))
                    .thenReturn(jimTypeCounts.size());

            ReservationInsertResponse response = reservationService.insertReservation(request);

            // then
            assertEquals(insertedId, response.getReservationId());
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
                when(lockerMapper.isExistLocker(request.getLockerId())).thenReturn(false);
                LockerException exception = assertThrows(LockerException.class, () -> {
                    reservationService.insertReservation(request);
                });

                // then
                assertEquals(NOT_FOUND_LOCKER, exception.getBaseResponseStatus());
            }

            @Test
            @DisplayName("startTime과 endTime이 같을 때")
            void 시작시간과_종료시간이_같음() {
                // given
                ReservationInsertRequest request = perfectRequest;
                request.setEndTime(request.getStartTime()); // startTime과 endTime을 동일하게 설정

                // when
                ReservationException exception = assertThrows(ReservationException.class, () -> {
                    reservationService.insertReservation(request);
                });

                // then
                assertEquals(INVALID_RESERVATION_TIME_ORDER, exception.getBaseResponseStatus());
            }

            @Test
            @DisplayName("startTime이 endTime보다 늦을 때")
            void 시작시간이_종료시간보다_큼() {
                // given
                ReservationInsertRequest request = perfectRequest;
                request.setEndTime("2025-10-02 09:00:00"); // startTime > endTime

                // when
                ReservationException exception = assertThrows(ReservationException.class, () -> {
                    reservationService.insertReservation(request);
                });

                // then
                assertEquals(INVALID_RESERVATION_TIME_ORDER, exception.getBaseResponseStatus());
            }

            @Test
            @DisplayName("dropper와 keeper가 동일할 때")
            void 맡길사람과_맡겨줄사람이_동일한_경우() {
                // given
                ReservationInsertRequest request = perfectRequest;
                // dropper와 keeper를 동일하게 설정
                when(lockerMapper.getLockerKeeperId(서울역_보관소.getLockerId())).thenReturn(맡김왕.getMemberId());

                // when
                when(lockerMapper.isExistLocker(request.getLockerId())).thenReturn(true);
                when(jimTypeMapper.validateLockerJimTypes(anyLong(), anyList(), anyInt())).thenReturn(true);
                ReservationException exception = assertThrows(ReservationException.class, () -> {
                    reservationService.insertReservation(request);
                });

                // then
                assertEquals(INVALID_RESERVATION_PARTICIPANTS, exception.getBaseResponseStatus());
            }

            @Nested
            @DisplayName("짐타입 검증 실패")
            class ValidateJimTypesFailure {
                @BeforeEach
                void setUp() {
//                    when(memberMapper.isExistMember(보관왕.getMemberId())).thenReturn(true);
//                    when(memberMapper.isExistMember(맡김왕.getMemberId())).thenReturn(true);
//                    when(lockerMapper.isExistLocker(서울역_보관소.getLockerId())).thenReturn(true);
//                    when(lockerMapper.isLockerKeeper(서울역_보관소.getLockerId(), 보관왕.getMemberId())).thenReturn(true);
                }

                @Test
                @DisplayName("존재하지 않는 짐타입에 예약을 시도할 때")
                void 존재하지_않는_짐타입() {
                    // given
                    ReservationInsertRequest request = Mockito.spy(perfectRequest);

                    request.setJimTypeCounts(Arrays.asList(
                            new JimTypeCountResult(백팩.getJimTypeId(), 1L),
                            new JimTypeCountResult(캐리어.getJimTypeId(), 3L),
                            new JimTypeCountResult(999L, 1L) // 존재하지 않는 짐타입 ID 추가
                    ));

                    // when
                    when(lockerMapper.isExistLocker(서울역_보관소.getLockerId())).thenReturn(true);
                    when(jimTypeMapper.validateLockerJimTypes(서울역_보관소.getLockerId(), List.of(백팩.getJimTypeId(), 캐리어.getJimTypeId(), 999L), 3))
                            .thenReturn(false);

                    JimTypeException exception = assertThrows(JimTypeException.class, () -> {
                        reservationService.insertReservation(request);
                    });

                    // then
                    assertEquals(LOCKER_DOES_NOT_SUPPORT_JIMTYPE, exception.getBaseResponseStatus());
                }

                @Test
                @DisplayName("보관소가 관리하지 않는 짐타입에 예약을 시도할 때")
                void 보관소가_관리하지_않는_짐타입() {
                    // given
                    ReservationInsertRequest request = Mockito.spy(perfectRequest);
                    request.setJimTypeCounts(Arrays.asList(
                            new JimTypeCountResult(백팩.getJimTypeId(), 1L),
                            new JimTypeCountResult(캐리어.getJimTypeId(), 3L),
                            new JimTypeCountResult(초대형캐리어.getJimTypeId(), 1L) // 존재하지 않는 짐타입 ID 추가
                    ));
                    // when
                    when(lockerMapper.isExistLocker(서울역_보관소.getLockerId())).thenReturn(true);
                    when(jimTypeMapper.validateLockerJimTypes(서울역_보관소.getLockerId(), List.of(백팩.getJimTypeId(), 캐리어.getJimTypeId(), 초대형캐리어.getJimTypeId()), 3))
                            .thenReturn(false);

                    JimTypeException exception = assertThrows(JimTypeException.class, () -> {
                        reservationService.insertReservation(request);
                    });

                    // then
                    assertEquals(LOCKER_DOES_NOT_SUPPORT_JIMTYPE, exception.getBaseResponseStatus());
                }

                @Test
                @DisplayName("요청한 짐 타입 개수와 실제 등록된 개수가 일치하지 않을 때")
                void 짐타임_등록_실패() {
                    // given
                    ReservationInsertRequest request = Mockito.spy(perfectRequest);

                    // when
                    when(lockerMapper.isExistLocker(서울역_보관소.getLockerId())).thenReturn(true);
                    when(jimTypeMapper.validateLockerJimTypes(서울역_보관소.getLockerId(), List.of(백팩.getJimTypeId(), 캐리어.getJimTypeId()), 2))
                            .thenReturn(true);
                    when(request.getId()).thenReturn(1L); // 예약 삽입 성공 시 id set 됨

                    when(jimTypeMapper.insertReservationJimTypes(1L, request.getJimTypeCounts())).thenReturn(0); // 등록 실패시 0 리턴

                    ReservationException exception = assertThrows(ReservationException.class, () -> {
                        reservationService.insertReservation(request);
                    });

                    // then
                    assertEquals(INVALID_JIMTYPE_COUNT, exception.getBaseResponseStatus());
                }
            }
        }

    }

    @Nested
    @DisplayName("예약 폼 받아오기 테스트")
    class GetReservationFormTest {
        Locker 임시_보관소;
        @BeforeEach
        void setUp(){
            임시_보관소 = Locker.builder()
                    .lockerId(4L)
                    .lockerName("임시 보관소")
                    .isAvailable(Available.YES)
                    .address("임시 임구 임시로 19-14")
                    .addressEnglish("temp tempro 19-14")
                    .addressDetail("503호")
                    .latitude(35.127197)
                    .longitude(126.912106)
                    .keeper(보관왕)
                    .build();
        }

        @Test
        @DisplayName("예약 폼 받아오기 성공")
        void 폼_받기_성공() {
            // given
            List<LockerJimTypeResult> jimTypes = 서울역_보관소.getLockerJimTypes().stream()
                    .map(LockerJimTypeResult::from)
                    .collect(Collectors.toList());

            // when
            when(lockerMapper.getLockerInfoById(임시_보관소.getLockerId()))
                    .thenReturn(ReservationFormResponse.from(임시_보관소));

            when(lockerMapper.getLockerJimTypeById(임시_보관소.getLockerId()))
                    .thenReturn(jimTypes);

            // then
            ReservationFormResponse response = reservationService.getReservationForm(임시_보관소.getLockerId());
            assertEquals(response.getLockerId(), 임시_보관소.getLockerId());

        }

        @Nested
        @DisplayName("실패")
        class fail{
            @Test
            @DisplayName("없는 보관소")
            void 없는_보관소() {
                LockerException exception = assertThrows(LockerException.class, () -> {
                    reservationService.getReservationForm(999L);
                });

                // then
                assertEquals(NOT_FOUND_LOCKER, exception.getBaseResponseStatus());
            }
            @Test
            @DisplayName("보관소 비활성화 상태")
            void 비활성화_보관소() {
                // given
                임시_보관소.setIsAvailable(Available.NO);

                // when
                when(lockerMapper.getIsAvailableById(임시_보관소.getLockerId())).thenReturn(Available.NO);

                LockerException exception = assertThrows(LockerException.class, () -> {
                    reservationService.getReservationForm(임시_보관소.getLockerId());
                });

                // then
                assertEquals(LOCKER_NOT_AVAILABLE, exception.getBaseResponseStatus());
            }
        }
    }
}