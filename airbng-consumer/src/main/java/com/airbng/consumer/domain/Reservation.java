package com.airbng.consumer.domain;

import com.airbng.consumer.exception.ReservationException;
import com.airbng.common.base.BaseStatus;
import com.airbng.common.base.BaseTime;
import com.airbng.consumer.domain.base.ReservationState;
import com.airbng.consumer.domain.jimtype.ReservationJimType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

import static com.airbng.platform.common.response.status.BaseResponseStatus.CANNOT_UPDATE_STATE;
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

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "locker_id", nullable = false)
    private Locker locker;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount = BigDecimal.ZERO;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal fee = BigDecimal.ZERO;

    @Column(nullable = false)
    private Long paymentId;

    public void addReservationJimType(ReservationJimType reservationJimType){
        reservationJimTypes.add(reservationJimType);
        reservationJimType.setReservation(this);
    }

    public void updateState(ReservationState state){
        this.state = state;
    }

    public void updateStatus(BaseStatus status){
        this.status = status;
    }

    public void isAvailableUpdateState(){
        if(status.equals(BaseStatus.DELETE))
            throw new ReservationException(CANNOT_UPDATE_STATE);
    }

}
