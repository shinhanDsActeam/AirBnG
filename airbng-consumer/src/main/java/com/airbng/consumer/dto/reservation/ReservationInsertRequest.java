package com.airbng.consumer.dto.reservation;

import com.airbng.consumer.domain.Locker;
import com.airbng.consumer.domain.Member;
import com.airbng.consumer.domain.Reservation;
import com.airbng.consumer.dto.jimType.JimTypeCountResult;
import com.airbng.common.base.BaseStatus;
import com.airbng.consumer.domain.base.ReservationState;
import com.airbng.consumer.domain.jimtype.ReservationJimType;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;

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


    public static ReservationJimTypeResult from(ReservationJimType reservationJimType){
        return ReservationJimTypeResult.builder()
                .typeName(reservationJimType.getJimType().getTypeName())
                .count(reservationJimType.getCount())
                .pricePerHour(reservationJimType.getJimType().getPricePerHour())
                .build();
    }

    public Reservation toEntity(Member dropper, Member keeper, Locker locker){
        return  Reservation.builder()
                .dropper(dropper)
                .keeper(keeper)
                .locker(locker)
                .startTime(startTime)
                .endTime(endTime)
                .state(ReservationState.PENDING)
                .status(BaseStatus.ACTIVE)
                .build();
    }

}
