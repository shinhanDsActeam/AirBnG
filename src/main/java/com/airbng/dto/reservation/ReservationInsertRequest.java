package com.airbng.dto.reservation;

import com.airbng.dto.jimType.JimTypeCountResult;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationInsertRequest {
    @JsonIgnore
    private Long id;
    private Long dropperId; // 맡길 사람 ID
    private Long keeperId;  // 맡길 짐을 보관하는 사람 ID
    private Long lockerId;  // 맡길 짐을 보관하는 락커 ID
    private String startTime; // 보관 시작 시간
    private String endTime;   // 회수해갈 시간
    private List<JimTypeCountResult> jimTypeCounts; // 맡길 짐 타입과 개수
}
