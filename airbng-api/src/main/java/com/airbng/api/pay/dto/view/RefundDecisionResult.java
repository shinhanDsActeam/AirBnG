package com.airbng.api.pay.dto.view;

import com.airbng.api.pay.dto.RefundStatus;

public record RefundDecisionResult(
        Long refundId,
        RefundStatus newStatus,
        Long decidedBy,
        java.time.Instant decidedAt,
        long finalRefundAmount, // 실제 환불된 금액
        long feeToKeeper
) {}