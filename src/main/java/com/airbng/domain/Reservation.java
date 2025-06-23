package com.airbng.domain;

import com.airbng.domain.base.BaseTime;
import com.airbng.domain.base.ReservationState;
import com.airbng.domain.jimtype.ReservationJimType;
import lombok.*;
import org.springframework.lang.NonNull;

import java.time.LocalDateTime;
import java.util.List;

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
    private List<ReservationJimType> reservationJimTypes;
}
