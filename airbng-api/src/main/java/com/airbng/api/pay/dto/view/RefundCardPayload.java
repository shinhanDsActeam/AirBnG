package com.airbng.api.pay.dto.view;

import com.airbng.api.pay.dto.RefundStatus;
import com.airbng.api.pay.dto.command.RefundType;

import java.time.Instant;

/**
 * Refund 엔티티 스냅샷 기반 카드 페이로드
 * - reason은 엔티티에 없으므로 제외
 * - refundType은 전액/일부 표시용으로 포함
 */
public record RefundCardPayload(
        Long refundId,
        Long reservationId,
        Long paymentId,
        Long lockerId,
        Long dropperId,        // payerId (환불 받는 사람)
        Long keeperId,         // payeeId (수수료 귀속 주체)
        long amount,           // refund_amount (환불 예정/완료 금액)
        long feeToKeeper,      // charge_fee (keeper 귀속 수수료)
        RefundType refundType, // FULL / PARTIAL
        Instant createdAt,
        RefundStatus status
) {}