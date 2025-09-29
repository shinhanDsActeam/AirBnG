package com.airbng.chat.dto.chat;

import com.airbng.api.pay.dto.view.RefundCardPayload;
import com.airbng.chat.domain.model.RefundCard;

import java.time.Instant;

public record RefundDto(
        Long refundId,
        Long reservationId,
        Long paymentId,
        Long dropperId,
        Long keeperId,
        Long lockerId,
        long amount,
        long feeToKeeper,
        String refundType,   // "FULL" / "PARTIAL"
        String status,       // "PENDING" / "COMPLETED" / "FAILED"
        Instant createdAt
) {
    /** Pay API의 뷰에서 Chat DTO로 변환 */
    public static RefundDto from(RefundCardPayload p) {
        if (p == null) return null;
        return new RefundDto(
                p.refundId(),
                p.reservationId(),
                p.paymentId(),
                p.dropperId(),
                p.keeperId(),
                p.lockerId(),
                p.amount(),
                p.feeToKeeper(),
                toStringSafe(p.refundType()),
                toStringSafe(p.status()),
                p.createdAt()
        );
    }

    /** Message 엔티티의 임베디드(혹은 JSON)에서 DTO로 변환 */
    public static RefundDto from(RefundCard f) {
        if (f == null) return null;
        return new RefundDto(
                f.getRefundId(),
                f.getReservationId(),
                f.getPaymentId(),
                f.getDropperId(),
                f.getKeeperId(),
                f.getLockerId(),
                nullSafe(f.getAmount()),
                nullSafe(f.getFeeToKeeper()),
                f.getRefundType(),
                f.getStatus(),
                f.getCreatedAt()
        );
    }

    private static String toStringSafe(Enum<?> e) { return e == null ? null : e.name(); }
    private static long nullSafe(Long v) { return v == null ? 0L : v; }
}
