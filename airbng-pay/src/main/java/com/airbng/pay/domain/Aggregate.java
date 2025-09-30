package com.airbng.pay.domain;

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
public class Aggregate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long aggregateId;

    @Column(nullable = false)
    private LocalDateTime aggregateDate;

    @Column(nullable = false)
    private BigDecimal totalSales;

    @Column(nullable = false)
    private Long totalCount;

    @Column(nullable = false)
    private BigDecimal averageSales;

    @Column(nullable = false)
    private BigDecimal totalFee;

    @Column(nullable = false)
    private Long lockerId;
}
