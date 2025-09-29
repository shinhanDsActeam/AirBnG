package com.airbng.api.pay.dto.view;

import com.airbng.common.base.PaymentMethod;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class PeriodSalesView {
    private Long settlementId;
    private BigDecimal amount;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime settlementDate;
    private PaymentMethod paymentMethod;
    private BigDecimal paymentFee;
    private Long reservationId;
    private Long keeperId;
    private LocalDateTime createdAt;
}