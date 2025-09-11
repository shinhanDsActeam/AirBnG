package com.airbng.core.dto.reservation;

import com.airbng.core.domain.Reservation;
import com.airbng.core.domain.base.ReservationState;
import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReservationCancelResponse {

    private Long reservationId;
    private ReservationState state;
    private Long charge;

    public static ReservationCancelResponse of(Reservation reservation, Long charge, ReservationState newState){
        return ReservationCancelResponse.builder()
                .reservationId(reservation.getReservationId())
                .state(newState)
                .charge(charge)
                .build();
    }

}
