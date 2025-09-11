package com.airbng.core.domain.jimtype;


import com.airbng.core.domain.Reservation;
import com.airbng.core.domain.base.BaseStatus;
import com.airbng.core.domain.base.BaseTime;
import jakarta.persistence.*;
import lombok.*;

import static jakarta.persistence.FetchType.LAZY;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationJimType extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reservationJimTypeId;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "jimtype_id",nullable = false)
    private JimType jimType;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "reservation_id",nullable = false)
    private Reservation reservation;

    @Column(nullable = false)
    private Long count;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "VARCHAR(10)")
    private BaseStatus status;

    public static ReservationJimType of(Reservation reservation, JimType jimType, long count){
        return ReservationJimType.builder()
                .reservation(reservation)
                .jimType(jimType)
                .count(count)
                .status(BaseStatus.ACTIVE)
                .build();
    }

}
