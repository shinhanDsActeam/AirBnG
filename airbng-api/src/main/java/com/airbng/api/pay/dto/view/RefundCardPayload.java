package com.airbng.api.pay.dto.view;

import com.airbng.api.pay.dto.RefundStatus;

public record RefundCardPayload(
        Long refundId,
        Long reservationId,
        Long paymentId,
        Long lockerId,           // 필요 시
        Long dropperId,
        Long keeperId,
        long amount,            // 환불 예정 금액(원금 또는 원금-수수료)
        long feeToKeeper,       // keeper에게 귀속될 수수료(승인 후)
        String reason,
        java.time.Instant requestedAt,
        RefundStatus status,
        boolean canDecide       // keeper에게만 true
) {}