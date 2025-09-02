package com.airbng.dto.reservation;

import com.airbng.domain.Member;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationResponse {

    private Long reservationId;       // 예약 ID
    private Member dropper;
    private Member keeper;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String state;
}
