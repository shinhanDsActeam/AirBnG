package com.airbng.dto.reservation;

import com.airbng.domain.base.ReservationState;
import com.airbng.dto.jimType.JimTypeResult;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;
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
    private String address;
    private String addressDetail;
    private String lockerImage;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;
    private String dateOnly;
    private Double durationHours;
//    private String itemNames; // 예: "가방, 신발"
    private List<JimTypeResult> jimTypeResults;
}
