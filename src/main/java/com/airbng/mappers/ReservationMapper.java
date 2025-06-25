package com.airbng.mappers;

import com.airbng.domain.Reservation;
import com.airbng.dto.reservation.ReservationInsertRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ReservationMapper {
    void insertReservation(ReservationInsertRequest reservation);
    Reservation findReservationDetailById(@Param("reservationId") Long reservationId);
}
