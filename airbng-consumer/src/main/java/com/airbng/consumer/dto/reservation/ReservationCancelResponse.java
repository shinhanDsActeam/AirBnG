package com.airbng.consumer.dto.reservation;

import com.airbng.consumer.domain.Reservation;
import com.airbng.consumer.domain.base.ReservationState;
import lombok.*;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReservationCancelResponse {

    private Long reservationId;
    private ReservationState state;
    private BigDecimal amount;
    private BigDecimal fee;
    private BigDecimal chargeFee;

    public static ReservationCancelResponse of(Reservation reservation, BigDecimal charge, ReservationState newState){
        return ReservationCancelResponse.builder()
                .reservationId(reservation.getReservationId())
                .state(newState)
                .amount(reservation.getAmount())
                .fee(reservation.getFee())
                .chargeFee(charge)
                .build();
    }

}
