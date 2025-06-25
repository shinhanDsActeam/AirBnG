package com.airbng.dto.reservation;

import com.airbng.domain.Reservation;
import com.airbng.domain.base.ReservationState;
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
