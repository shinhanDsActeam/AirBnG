package com.airbng.dto.reservation;

import com.airbng.domain.Reservation;
import com.airbng.domain.base.Available;
import com.airbng.domain.base.ReservationState;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationDetailResponse {
    private Long reservationId;
    private String dropperNickname;
    private String keeperNickname;
    private String startTime;
    private String endTime;
    private ReservationState state;
    private List<ReservationJimTypeResult> reservationJimTypes;

    public static ReservationDetailResponse from(Reservation reservation){
        return ReservationDetailResponse.builder()
                .reservationId(reservation.getReservationId())
                .dropperNickname(reservation.getDropper().getNickname())
                .keeperNickname(reservation.getKeeper().getNickname())
                .startTime(String.valueOf(reservation.getStartTime()))
                .state(reservation.getState())
                .endTime(String.valueOf(reservation.getEndTime()))
                .reservationJimTypes(
                        reservation.getReservationJimTypes().stream()
                                .map(ReservationJimTypeResult::from)
                                .collect(Collectors.toList())
                )
                .build();
    }
}
