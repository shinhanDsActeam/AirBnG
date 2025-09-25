package com.airbng.consumer.dto.reservation;

import com.airbng.api.pay.dto.RefundStatus;
import com.airbng.api.pay.dto.command.RefundType;
import com.airbng.consumer.domain.Reservation;
import com.airbng.consumer.domain.base.ReservationState;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;

@Getter
@Builder
public class ReservationCancelResponse {
    private Long reservationId;
    private String state;
    private long chargeFee;          // 정책상 차감 수수료(원)
    private RefundSummary refund;    // ← 환불 요약

    @Getter
    @Builder
    public static class RefundSummary {
        private Long refundId;
        private long amount;          // 환불 예정/완료 금액(원)
        private RefundType refundType; // FULL / PARTIAL
        private RefundStatus status;   // REQUESTED / PROCESSED …
        private Instant createdAt;     // 카드 표시에 유용
    }

    public static ReservationCancelResponse of(Reservation r,
                                               BigDecimal chargeFee,
                                               ReservationState state,
                                               RefundSummary refund) {
        return ReservationCancelResponse.builder()
                .reservationId(r.getReservationId())
                .state(state.name())
                .chargeFee(chargeFee == null ? 0L : chargeFee.setScale(0, RoundingMode.HALF_UP).longValueExact())
                .refund(refund)
                .build();
    }
}
