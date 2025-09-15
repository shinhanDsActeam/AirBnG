package com.airbng.consumer.dto.reservation;

import com.airbng.consumer.domain.jimtype.ReservationJimType;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationJimTypeResult {
    private String typeName;
    private Long count;
    private Long pricePerHour;

    public static ReservationJimTypeResult from(ReservationJimType reservationJimType){
        return ReservationJimTypeResult.builder()
                .typeName(reservationJimType.getJimType().getTypeName())
                .count(reservationJimType.getCount())
                .pricePerHour(reservationJimType.getJimType().getPricePerHour())
                .build();
    }
}
