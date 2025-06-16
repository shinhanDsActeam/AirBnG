package com.airbng.domain;

import java.time.LocalDateTime;

import com.airbng.domain.base.BaseTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Builder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reservation extends BaseTime {
    private Long reservationId;
    private Member dropper;
    private Member keeper;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String status;
}
