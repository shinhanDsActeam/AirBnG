package com.airbng.domain.jimtype;


import com.airbng.domain.Reservation;
import com.airbng.domain.base.BaseStatus;
import com.airbng.domain.base.BaseTime;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.lang.NonNull;

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

}
