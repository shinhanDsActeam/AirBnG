package com.airbng.dto.reservation;

import com.airbng.domain.Member;
import com.airbng.domain.Reservation;
import com.airbng.domain.base.ReservationState;
import lombok.*;

import java.time.LocalDateTime;

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
    private ReservationState state;


    public static ReservationResponse from(Reservation r) {
        ReservationResponse dto = new ReservationResponse();
        dto.reservationId = r.getReservationId();
        dto.dropper = r.getDropper();
        dto.keeper = r.getKeeper();
        dto.endTime = r.getEndTime();
        dto.state = r.getState();
        return dto;
    }
}
