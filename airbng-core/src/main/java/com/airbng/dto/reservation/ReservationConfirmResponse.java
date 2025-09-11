package com.airbng.dto.reservation;

import com.airbng.domain.Reservation;
import com.airbng.domain.base.ReservationState;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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
