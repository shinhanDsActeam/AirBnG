package com.airbng.domain;

import com.airbng.domain.base.BaseStatus;
import com.airbng.domain.base.BaseTime;
import com.airbng.domain.base.ReservationState;
import com.airbng.domain.jimtype.ReservationJimType;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.lang.NonNull;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static jakarta.persistence.FetchType.LAZY;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reservation extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reservationId;

    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "dropper_id", nullable = false)
    private Member dropper;

    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "keeper_id", nullable = false)
    private Member keeper;

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = false)
    private LocalDateTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReservationState state;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "VARCHAR(10)")
    private BaseStatus status;

    @Builder.Default
    @OneToMany(mappedBy = "reservation", cascade = CascadeType.ALL)
    private Set<ReservationJimType> reservationJimTypes = new LinkedHashSet<>();

    public void addReservationJimType(ReservationJimType reservationJimType){
        reservationJimTypes.add(reservationJimType);
        reservationJimType.setReservation(this);
    }

}
