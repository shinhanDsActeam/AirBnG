package com.airbng.dto.reservation;

import com.airbng.domain.base.ReservationState;
import com.airbng.dto.jimType.JimTypeResult;
import lombok.*;

import java.util.List;

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
    private String lockerName;
    private String lockerImage;
    private String startTime;
    private String endTime;
    private String dateOnly;
    private Double durationHours;
//    private String itemNames; // 예: "가방, 신발"
    private List<JimTypeResult> jimTypeResults;
}
