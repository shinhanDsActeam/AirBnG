package com.airbng.dto.reservation;

import com.airbng.domain.Locker;
import com.airbng.domain.Reservation;
import com.airbng.dto.jimType.LockerJimTypeResult;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationFormResponse {
    private Long lockerId;
    private String lockerName;
    private String addressKr;
    private List<LockerJimTypeResult> lockerJimTypes;

    public static ReservationFormResponse from(Locker locker){
        return ReservationFormResponse.builder()
                .lockerName(locker.getLockerName())
                .lockerId(locker.getLockerId())
                .addressKr(locker.getAddress() + " " +locker.getAddressDetail())
                .build();
    }
}
