package com.airbng.dto.reservation;

import com.airbng.domain.Member;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationResponse {

    private Long reservationId;       // 예약 ID
    private Member dropper;
    private Member keeper;
    private String startTime;
    private String endTime;
    private String state;
}
