package com.airbng.mappers;

import com.airbng.domain.base.ReservationState;
import com.airbng.domain.Reservation;
import org.apache.ibatis.annotations.Param;
import com.airbng.dto.reservation.ReservationInsertRequest;
import com.airbng.dto.reservation.ReservationSearchResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

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
                                             @Param("state") String state,
                                             @Param("nextCursorId") Long nextCursorId,
                                             @Param("limit") Long limit,
                                             @Param("period") String period
    );
    //예약  목록 개수
    Long findReservationByMemberId(@Param("memberId") Long memberId, @Param("role") String role);
    void deleteReservationById(@Param("reservationId")Long reservationId);
    void deleteReservationJimtypeByReservationId(@Param("reservationId")Long reservationId);
    ReservationState findReservationStateById(Long reservationId);

}
