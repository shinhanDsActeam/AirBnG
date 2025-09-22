package com.airbng.consumer.dto.reservation;

import com.airbng.consumer.domain.Reservation;
import com.airbng.consumer.domain.base.ReservationState;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReservationCompleteResponse {
    private Long reservationId;
    private ReservationState reservationState;

    public static ReservationCompleteResponse from(Reservation reservation) {
        return ReservationCompleteResponse.builder()
                .reservationId(reservation.getReservationId())
                .reservationState(reservation.getState())
                .build();
    }
}
