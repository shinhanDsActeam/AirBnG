package com.airbng.dto.reservation;

import com.airbng.dto.jimType.LockerJimTypeResult;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReservationFormResponse {
    private Long lockerId;
    private String lockerName;
    private String addressKr;
    private List<LockerJimTypeResult> lockerJimTypes;
}
