package com.airbng.dto.reservation;

import com.airbng.dto.jimType.JimTypeCountResult;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationInsertRequest {
    private Long dropperId;

    private Long keeperId;

    @NotNull
    @Min(1)
    private Long lockerId;  // 맡길 짐을 보관하는 락커 ID

    @NotNull @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime; // 보관 시작 시간

    @NotNull @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;   // 회수해갈 시간

    @Valid
    private List<JimTypeCountResult> jimTypeCounts; // 맡길 짐 타입과 개수



}
