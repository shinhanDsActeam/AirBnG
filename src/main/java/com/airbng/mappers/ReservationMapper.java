package com.airbng.mappers;

import com.airbng.domain.Reservation;
import com.airbng.domain.base.ReservationState;
import org.apache.ibatis.annotations.Param;

public interface ReservationMapper {
    void updateReservationState(@Param("reservationId")Long reservationId, @Param("state")ReservationState state);
    Reservation findReservationWithDropperById(Long reservationId);
}
