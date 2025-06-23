package com.airbng.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationSearchResponse {
    private Long reservationId;
    private Long keeperId;
    private Long dropperId;
    private String state;
    private String role;   // "KEEPER" or "DROPPER"
}
