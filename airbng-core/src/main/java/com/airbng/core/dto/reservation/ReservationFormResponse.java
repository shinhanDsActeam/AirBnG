package com.airbng.core.dto.reservation;

import com.airbng.core.domain.Locker;
import com.airbng.core.dto.jimType.LockerJimTypeResult;
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
                .lockerJimTypes(
                        locker.getLockerJimTypes().stream()
                                .map(LockerJimTypeResult::from)
                                .collect(Collectors.toList())
                )
                .build();
    }
}
