package com.airbng.api.pay.dto.command;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
@Getter
public class RefundRequestCommand{
    UUID idemKey;        // 멱등키
    Long reservationId;
    Long paymentId;     // dropper
    BigDecimal chargeFee; // 환불 정책 수수료
    RefundType refundType;
}