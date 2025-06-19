package com.airbng.domain;

import java.time.LocalDateTime;

import com.airbng.domain.base.BaseTime;
import com.airbng.domain.base.ReservationState;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Builder;
import org.springframework.lang.NonNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reservation extends BaseTime {
//    @NonNull
    private Long reservationId;
    @NonNull
    private Member dropper;
    @NonNull
    private Member keeper;
    @NonNull
    private LocalDateTime startTime;
    @NonNull
    private LocalDateTime endTime;
    @NonNull
    private ReservationState state;
}
