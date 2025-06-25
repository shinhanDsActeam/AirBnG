package com.airbng.mappers;

import com.airbng.domain.Reservation;
import com.airbng.domain.base.ReservationState;
import com.airbng.dto.reservation.ReservationInsertRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ReservationMapper {
    void updateReservationState(@Param("reservationId")Long reservationId, @Param("state")ReservationState state);
    Reservation findReservationWithDropperById(Long reservationId);
    void insertReservation(ReservationInsertRequest reservation);
    Reservation findReservationDetailById(@Param("reservationId") Long reservationId);
}
