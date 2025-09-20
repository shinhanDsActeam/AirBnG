package com.airbng.consumer.dto.reservation;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationInsertResponse {
    private Long reservationId;

    public static ReservationInsertResponse from(Long reservationId){
        return ReservationInsertResponse.builder()
                .reservationId(reservationId)
                .build();
    }
}
