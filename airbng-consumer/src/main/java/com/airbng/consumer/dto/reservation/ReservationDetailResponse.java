package com.airbng.consumer.dto.reservation;

import com.airbng.consumer.domain.Reservation;
import com.airbng.consumer.domain.base.ReservationState;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationDetailResponse {
    private Long reservationId;
    private String lockerName;
    private String dropperNickname;
    private String keeperNickname;
    private Long keeperId;
    private Long dropperId;
    private String startTime;
    private String endTime;
    private List<String> images;
    private ReservationState state;
    private List<ReservationJimTypeResult> reservationJimTypes;
    private String fullAddress;

    public static ReservationDetailResponse from(Reservation reservation){
        return ReservationDetailResponse.builder()
                .reservationId(reservation.getReservationId())
                .lockerName(reservation.getLocker().getLockerName())
                .fullAddress(reservation.getLocker().getAddress() + " " + reservation.getLocker().getAddressDetail())
                .dropperNickname(reservation.getDropper().getNickname())
                .keeperNickname(reservation.getKeeper().getNickname())
                .keeperId(reservation.getKeeper().getMemberId())
                .dropperId(reservation.getDropper().getMemberId())
                .startTime(String.valueOf(reservation.getStartTime()))
                .state(reservation.getState())
                .endTime(String.valueOf(reservation.getEndTime()))
                .fullAddress(reservation.getLocker().getAddress() + " " + reservation.getLocker().getAddressDetail())
                .reservationJimTypes(
                        reservation.getReservationJimTypes().stream()
                                .map(ReservationJimTypeResult::from)
                                .collect(Collectors.toList())
                )
                .images(reservation.getLocker().getLockerImages().stream()
                        .map(lockerImage -> lockerImage.getImage().getUrl())
                        .collect(Collectors.toList()))
                .build();
    }
}
