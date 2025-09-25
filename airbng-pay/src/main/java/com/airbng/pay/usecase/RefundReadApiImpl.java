package com.airbng.pay.usecase;

import com.airbng.api.pay.RefundReadApi;
import com.airbng.api.pay.dto.RefundStatus;
import com.airbng.api.pay.dto.command.RefundType;
import com.airbng.api.pay.dto.view.RefundCardPayload;
import com.airbng.pay.domain.Refund;
import com.airbng.pay.repository.RefundRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.ZoneId;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RefundReadApiImpl implements RefundReadApi {

    private final RefundRepository refundRepository;

    @Override
    public RefundCardPayload getCardPayload(Long refundId) {
        Refund r = refundRepository.findById(refundId)
                .orElseThrow(() -> new IllegalArgumentException("refund not found: " + refundId));

        long feeToKeeper = toLongKRW(r.getChargeFee());
        long amount = toLongKRW(r.getRefundAmount());

        return new RefundCardPayload(
                r.getRefundId(),
                r.getReservationId(),
                r.getPaymentId(),
                r.getLockerId(),
                r.getPayerId(),    // dropper
                r.getPayeeId(),    // keeper
                amount,
                feeToKeeper,
                RefundType.valueOf(r.getRefundType().name()), // FULL / PARTIAL
                r.getCreatedAt().atZone(ZoneId.systemDefault()).toInstant(),
                mapStatus(r.getRefundStatus())
        );
    }

    /** KRW 기준 소수 없다는 전제 -> 안전하게 null 방지 + 반올림 처리 */
    private long toLongKRW(BigDecimal v) {
        if (v == null) return 0L;
        // 소수 스케일이 들어올 수 있으니 반올림 후 long 변환
        return v.setScale(0, RoundingMode.HALF_UP).longValueExact();
    }

    private RefundStatus mapStatus(com.airbng.pay.domain.RefundStatus s) {
        if (s == null) return RefundStatus.FAILED;
        switch (s) {
            case REQUESTED:  return RefundStatus.PENDING;
            case PROCESSED:  return RefundStatus.COMPLETED;
            // 추후 도메인 상태가 늘면 여기에 추가
            default:         return RefundStatus.FAILED;
        }
    }
}
