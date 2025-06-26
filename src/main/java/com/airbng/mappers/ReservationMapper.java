package com.airbng.mappers;

import com.airbng.domain.base.ReservationState;
import com.airbng.domain.Reservation;
import org.apache.ibatis.annotations.Param;
import com.airbng.dto.reservation.ReservationInsertRequest;
import com.airbng.dto.reservation.ReservationSearchResponse;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface ReservationMapper {

    void updateReservationState(@Param("reservationId")Long reservationId, @Param("state")ReservationState state);
    Reservation findReservationWithDropperById(Long reservationId);
  
    void insertReservation(ReservationInsertRequest reservation);

    // 예약 조회 + 페이징 처리
    List<ReservationSearchResponse> findAllReservationById(
                                             @Param("memberId") Long memberId,
                                             @Param("role") String role,
                                             @Param("state") String state,
                                             @Param("nextCursorId") Long nextCursorId,
                                             @Param("limit") Long limit
    );

    //예약  목록 개수
    Long findReservationByMemberId(@Param("memberId") Long memberId, @Param("role") String role);

    // 단건 예약 조회
    Reservation findById(Long id);

    // 24시간이 지난 CONFIRMED 예약 조회 (EXPIRED 알림용)
    List<ReservationSearchResponse> findExpiredConfirmedReservations(@Param("deadline") LocalDateTime deadline);

    // endTime이 30분 이내인 예약 조회 (REMINDER 알림용)
    List<ReservationSearchResponse> findConfirmedNearEndTime(@Param("now") LocalDateTime now);

    // 최근 5분 이내 CONFIRMED로 변경된 예약 (STATE_CHANGE 알림용)
    List<ReservationSearchResponse> findStateChangedToConfirmed(@Param("since") LocalDateTime since);

    // 최근 5분 이내 CANCELLED로 변경된 예약 (CANCEL_NOTICE 알림용)
    List<ReservationSearchResponse> findStateChangedToCancelled(@Param("since") LocalDateTime since);

}
