package com.airbng.pay.domain;

import com.airbng.common.base.PaymentMethod;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Settlement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long settlementId;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    private LocalDateTime settlementDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMethod paymentMethod;

    @Column(nullable = false)
    private BigDecimal paymentFee;

    @Column(nullable = false)
    private Long reservationId;

    @Column(nullable = false)
    private Long keeperId;

    @Column(nullable = false)
    private LocalDateTime createdAt;
}
