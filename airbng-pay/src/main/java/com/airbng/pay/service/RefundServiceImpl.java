package com.airbng.pay.service;

import com.airbng.api.pay.dto.RefundStatus; // API enum
import com.airbng.api.pay.dto.command.RefundDecisionCommand;
import com.airbng.api.pay.dto.command.RefundRequestCommand;
import com.airbng.api.pay.dto.view.RefundDecisionResult;
import com.airbng.pay.domain.*;
import com.airbng.pay.repository.PaymentRepository;
import com.airbng.pay.repository.RefundRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class RefundServiceImpl implements RefundService {

    private final RefundRepository refundRepo;
    private final PaymentRepository paymentRepo;
    private final RefundPolicy policy;
    private final EntityManager em;

    @Override
    public Refund requestRefund(RefundRequestCommand cmd) {
        // 1) 멱등
        var existing = refundRepo.findByBizKey(cmd.idemKey());
        if (existing.isPresent()) return existing.get();

        // 2) 결제 조회
        Payment payment = paymentRepo.findById(cmd.paymentId())
                .orElseThrow(() -> new IllegalArgumentException("payment not found: " + cmd.paymentId()));

        // 3) 수수료 계산(모드 기반)
        BigDecimal chargeFee = policy.computeChargeFee(payment, cmd.mode());

        // 4) refund_amount = amount + fee - charge_fee
        BigDecimal refundAmount = payment.getPaymentAmount()
                .add(payment.getPaymentFee())
                .subtract(chargeFee);
        if (refundAmount.signum() < 0) refundAmount = BigDecimal.ZERO;

        // 5) 엔티티 생성(REQUESTED)
        Refund refund = Refund.builder()
                .reservationId(cmd.reservationId())
                .payment(payment)
                .refundReason(cmd.reason())
                .payerId(payment.getPayerId())
                .payeeId(payment.getPayeeId())
                .lockerId(payment.getLockerId())
                .refundAmount(refundAmount)
                .chargeFee(chargeFee)
                .refundType(chargeFee.signum()==0 ? RefundType.FULL : RefundType.PARTIAL)
                .refundStatus(com.airbng.pay.domain.RefundStatus.REQUESTED)
                .requestedAt(LocalDateTime.now())
                .bizKey(cmd.idemKey())
                .build();

        refund = refundRepo.save(refund);

        // (옵션) AUTO_FULL이면 지갑 즉시 처리 후 PROCESSED 마킹
        // if (cmd.mode() == RefundMode.AUTO_FULL) { processWalletRefund(refund); }

        return refund;
    }

    @Override
    public RefundDecisionResult decide(RefundDecisionCommand cmd) {
        Refund refund = refundRepo.findById(cmd.refundId())
                .orElseThrow(() -> new IllegalArgumentException("refund not found: " + cmd.refundId()));

        if (!refund.getPayeeId().equals(cmd.actorId()))
            throw new IllegalStateException("only keeper can decide");

        if (refund.isProcessed()) return toDecisionResult(refund, cmd.actorId());

        if (cmd.approve()) {
            // processWalletRefund(refund); // 실제 원장 처리
            refund.markProcessed(LocalDateTime.now());
        } // 거절 시 상태 유지는 요구사항대로

        return toDecisionResult(refund, cmd.actorId());
    }

    private RefundDecisionResult toDecisionResult(Refund refund, Long actorId) {
        return new RefundDecisionResult(
                refund.getRefundId(),
                RefundStatus.valueOf(refund.getRefundStatus().name()),
                actorId,
                refund.getProcessedAt() != null
                        ? refund.getProcessedAt().atZone(java.time.ZoneId.systemDefault()).toInstant()
                        : null,
                refund.getRefundAmount().longValue(),
                refund.getChargeFee().longValue()
        );
    }
}
