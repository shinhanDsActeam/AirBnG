package com.airbng.pay.domain.view;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Subselect;
import org.hibernate.annotations.Synchronize;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Immutable
@Subselect("""
        SELECT
          r.reservation_id,
          r.payment_id,
          r.keeper_id,
          r.locker_id,
          r.updated_at as completed_at,
          p.payment_amount as amount,
          p.payment_fee as fee
        FROM reservation r
        JOIN payment p ON p.payment_id = r.payment_id
        WHERE r.state = 'COMPLETED'
        """)
@Synchronize({"reservation", "payment"})
@Getter
@Table(name = "completed_reservation_view")
public class CompletedReservationView {
    @Id
    private Long reservationId;

    private Long paymentId;

    private Long keeperId;

    private Long lockerId;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    private BigDecimal amount;

    private BigDecimal fee;
}