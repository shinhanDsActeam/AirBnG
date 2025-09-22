package com.airbng.pay.domain;

import com.airbng.common.base.BaseTime;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentId;

    @Column(nullable = false)
    private Long payerId; // 결제자 : dropper

    @Column(nullable = false)
    private Long payeeId; // 피결제자 : keeper

    @Column(nullable = false)
    private Long lockerId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PayMethod method; // WALLET, PG

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus paymentStatus; // 결제상태

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal paymentAmount; // 결제 금액

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal paymentFee; // 수수료

    @Column(columnDefinition = "BINARY(16)", nullable = false, unique = true)
    private UUID payIdemKey; // 멱등키
}
