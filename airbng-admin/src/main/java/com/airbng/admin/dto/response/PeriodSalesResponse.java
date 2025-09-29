package com.airbng.admin.dto.response;


import com.airbng.api.pay.dto.view.PeriodSalesView;
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

    public static PeriodSalesResponse from(PeriodSalesView view) {
        return PeriodSalesResponse.builder()
                .settlementId(view.getSettlementId())
                .amount(view.getAmount())
                .settlementDate(view.getSettlementDate())
                .paymentMethod(view.getPaymentMethod())
                .paymentFee(view.getPaymentFee())
                .reservationId(view.getReservationId())
                .keeperId(view.getKeeperId())
                .createdAt(view.getCreatedAt())
                .build();
    }
}
