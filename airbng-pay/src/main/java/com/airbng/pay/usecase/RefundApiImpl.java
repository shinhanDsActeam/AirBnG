package com.airbng.pay.usecase;

import com.airbng.api.pay.RefundApi;
import com.airbng.api.pay.dto.command.RefundRequestCommand;
import com.airbng.pay.domain.Payment;
import com.airbng.pay.domain.Refund;
import com.airbng.pay.domain.RefundStatus;
import com.airbng.pay.domain.RefundType;
import com.airbng.pay.exception.PaymentException;
import com.airbng.pay.repository.PaymentRepository;
import com.airbng.pay.repository.RefundRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static com.airbng.platform.common.response.status.BaseResponseStatus.FAILED_PAYMENT;
import static com.airbng.platform.common.response.status.BaseResponseStatus.NOT_FOUND_PAYMENT;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RefundApiImpl implements RefundApi {

    private final RefundRepository refundRepository;
    private final PaymentRepository paymentRepository;

    @Override
    @Transactional
    public Long requestRefund(RefundRequestCommand cmd) {
        Refund existed = refundRepository.findByBizKey(cmd.getIdemKey()).orElse(null);
        if (existed != null) { // 멱등
            return existed.getRefundId();
        }

        Payment p = paymentRepository.findById(cmd.getPaymentId())
                .orElseThrow(() -> new PaymentException(NOT_FOUND_PAYMENT));

        RefundType refundType = null;
        if (cmd.getChargeFee().compareTo(BigDecimal.ZERO) == 0) {
            refundType = RefundType.FULL;
        } else {
            refundType = RefundType.PARTIAL;
        }

        final Refund refund;

        try {
            refund = Refund.builder()
                    .reservationId(cmd.getReservationId())
                    .refundAmount(p.getPaymentAmount().add(p.getPaymentFee()).subtract(cmd.getChargeFee()))
                    .refundStatus(RefundStatus.REQUESTED)
                    .refundType(refundType)
                    .chargeFee(cmd.getChargeFee())
                    .payeeId(p.getPayeeId())
                    .payerId(p.getPayerId())
                    .lockerId(p.getLockerId())
                    .build();
            refundRepository.save(refund);

        return refund.getRefundId();
    }
}
