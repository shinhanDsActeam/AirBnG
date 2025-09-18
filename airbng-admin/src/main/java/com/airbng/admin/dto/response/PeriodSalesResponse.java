package com.airbng.admin.dto.response;


import com.airbng.common.base.PaymentMethod;
import com.airbng.common.domain.Settlement;
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
    private BigDecimal sales;
    private LocalDateTime updatedAt;
    private PaymentMethod paymentMethod;
    private BigDecimal paymentFee;

    public static PeriodSalesResponse from(Settlement settlement) {
        return PeriodSalesResponse.builder()
                .settlementId(settlement.getSettlementId())
                .sales(settlement.getSales())
                .updatedAt(settlement.getSettlementDate())
                .paymentMethod(settlement.getPaymentMethod())
                .paymentFee(settlement.getPaymentFee())
                .build();
    }
}
