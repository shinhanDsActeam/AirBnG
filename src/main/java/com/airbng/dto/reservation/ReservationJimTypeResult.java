package com.airbng.dto.reservation;

import com.airbng.domain.Reservation;
import com.airbng.domain.jimtype.JimType;
import com.airbng.domain.jimtype.ReservationJimType;
import lombok.*;
import org.springframework.lang.NonNull;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationJimTypeResult {
    private String typeName;
    private Long count;

    public static ReservationJimTypeResult from(ReservationJimType reservationJimType){
        return ReservationJimTypeResult.builder()
                .typeName(reservationJimType.getJimType().getTypeName())
                .count(reservationJimType.getCount())
                .build();
    }
}
