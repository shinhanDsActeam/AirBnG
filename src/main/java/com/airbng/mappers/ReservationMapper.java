package com.airbng.mappers;

import com.airbng.domain.base.ReservationState;
import com.airbng.domain.Reservation;
import com.airbng.dto.reservation.ReservationResponse;
import org.apache.ibatis.annotations.Param;
import com.airbng.dto.reservation.ReservationInsertRequest;
import com.airbng.dto.reservation.ReservationSearchResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface ReservationMapper {

    void updateReservationState(@Param("reservationId")Long reservationId, @Param("state")ReservationState state);
    Reservation findReservationWithDropperById(Long reservationId);
    Reservation findReservationWithKeeperById(Long reservationId);

    void insertReservation(ReservationInsertRequest reservation);
    Reservation findReservationDetailById(@Param("reservationId") Long reservationId);
    // 예약 조회 + 페이징 처리
    List<ReservationSearchResponse> findAllReservationById(
                                             @Param("memberId") Long memberId,
                                             @Param("role") String role,
                                             @Param("state")  List<ReservationState> state,
                                             @Param("nextCursorId") Long nextCursorId,
                                             @Param("limit") Long limit,
                                             @Param("period") String period,
                                             @Param("isHistoryTab") boolean isHistoryTab
    );
  
    //예약  목록 개수
    Long findReservationByMemberId(@Param("memberId") Long memberId, @Param("role") String role);

    // 24시간이 지난 CONFIRMED 예약 조회 (EXPIRED 알림용)
    List<ReservationResponse> findExpiredConfirmedReservations(@Param("deadline") LocalDateTime deadline);

    // endTime이 30분 이내인 예약 조회 (REMINDER 알림용)
    List<ReservationResponse> findConfirmedNearEndTime(@Param("now") LocalDateTime now);

    // CONFIRMED로 변경된 예약 (STATE_CHANGE 알림용)
    List<ReservationResponse> findStateChangedToConfirmed();

    // CANCELLED로 변경된 예약 (CANCEL_NOTICE 알림용)
    List<ReservationResponse> findStateChangedToCancelled();

    Long findAllReservationByMemberId();

    void deleteReservationById(@Param("reservationId")Long reservationId);
    void deleteReservationJimtypeByReservationId(@Param("reservationId")Long reservationId);
    ReservationState findReservationStateById(Long reservationId);
}
