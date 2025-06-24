package com.airbng.domain.jimtype;


import com.airbng.domain.Reservation;
import com.airbng.domain.base.BaseTime;
import lombok.*;
import org.springframework.lang.NonNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationJimType extends BaseTime {
//    @NonNull
    private Long reservationJimTypeId;
    @NonNull
    private JimType jimType;
    @NonNull
    private Reservation reservation;
    @NonNull
    private Long count;

}
