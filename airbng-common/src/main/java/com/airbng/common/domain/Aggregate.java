package com.airbng.common.domain;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Aggregate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long aggregateId;

    @Column(nullable = false)
    private LocalDate aggregateDate;

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
