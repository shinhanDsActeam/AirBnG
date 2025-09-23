// src/main/java/com/airbng/pay/domain/Refund.java
package com.airbng.pay.domain;

import com.airbng.common.base.BaseTime;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "refund", indexes = {
        @Index(name = "idx_refund_reservation", columnList = "reservation_id"),
        @Index(name = "idx_refund_payment", columnList = "payment_id"),
        @Index(name = "idx_refund_status_date", columnList = "refund_status, created_at"),
        @Index(name = "idx_refund_locker", columnList = "locker_id")
})
public class Refund extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "refund_id")
    private Long refundId;

    /** reservation FK (consumer 쪽 엔티티를 직접 참조하지 않고 Long으로 보관) */
    @Column(name = "reservation_id", nullable = false)
    private Long reservationId;

    /** 스냅샷 용도 (반정규화) */
    private Long paymentId;

    /** 환불/수수료 정산 대상들 */
    @Column(name = "payer_id", nullable = false)
    private Long payerId;   // dropper(환불 받는 사람)

    @Column(name = "payee_id", nullable = false)
    private Long payeeId;   // keeper(수수료 수익 주체)

    @Column(name = "locker_id", nullable = false)
    private Long lockerId;

    /** 금액 필드 */
    @Column(name = "refund_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal refundAmount;  // dropper에게 돌아갈 금액

    @Column(name = "charge_fee", nullable = false, precision = 19, scale = 2)
    private BigDecimal chargeFee;     // 정책상 차감 수수료(없으면 0)

    /** 타입/상태 */
    @Enumerated(EnumType.STRING)
    @Column(name = "refund_type", nullable = false, length = 16)
    private RefundType refundType; // FULL / PARTIAL

    @Enumerated(EnumType.STRING)
    @Column(name = "refund_status", nullable = false, length = 16)
    private RefundStatus refundStatus; // REQUESTED / PROCESSED

    /** 멱등 키 */
    @Column(name = "biz_key", columnDefinition = "BINARY(16)", nullable = false, unique = true)
    private UUID bizKey;

    /* ===== 도메인 메서드 ===== */
    public boolean isProcessed() {
        return this.refundStatus == RefundStatus.PROCESSED;
    }
}
