package com.airbng.domain;

import com.airbng.domain.base.BaseStatus;
import com.airbng.domain.base.BaseTime;
import com.airbng.domain.base.ReservationState;
import com.airbng.domain.jimtype.ReservationJimType;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.lang.NonNull;

import java.time.LocalDateTime;
import java.util.List;

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

    @OneToMany(mappedBy = "reservation")
    private List<ReservationJimType> reservationJimTypes;
}
