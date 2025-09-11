package com.airbng.core.dto.reservation;

import com.airbng.core.domain.Reservation;
import com.airbng.core.domain.base.ReservationState;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReservationConfirmResponse {
    private Long reservationId;
    private ReservationState state;

    public static ReservationConfirmResponse of(Reservation reservation, ReservationState newState) {
        return ReservationConfirmResponse.builder()
                .reservationId(reservation.getReservationId())
                .state(newState)
                .build();
    }
}
