package com.airbng.admin.dto.response;


import com.airbng.common.base.PaymentMethod;
import com.airbng.common.domain.Settlement;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class PeriodSalesResponse {
    private Long settlementId;
    private BigDecimal amount;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime settlementDate;
    private PaymentMethod paymentMethod;
    private BigDecimal paymentFee;
    private Long reservationId;
    private Long keeperId;
    private LocalDateTime createdAt;

    public static PeriodSalesResponse from(Settlement settlement) {
        return PeriodSalesResponse.builder()
                .settlementId(settlement.getSettlementId())
                .amount(settlement.getAmount())
                .settlementDate(settlement.getSettlementDate())
                .paymentMethod(settlement.getPaymentMethod())
                .paymentFee(settlement.getPaymentFee())
                .reservationId(settlement.getReservationId())
                .keeperId(settlement.getKeeperId())
                .createdAt(settlement.getCreatedAt())
                .build();
    }
}
