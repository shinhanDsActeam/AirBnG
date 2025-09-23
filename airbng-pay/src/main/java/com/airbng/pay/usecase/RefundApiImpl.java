package com.airbng.pay.usecase;

import com.airbng.api.pay.RefundApi;
import com.airbng.api.pay.dto.command.RefundRequestCommand;
import com.airbng.pay.domain.Refund;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RefundApiImpl implements RefundApi {

    private final RefundRepository refundRepository;
    private final PaymentRepository paymentRepository;

    @Override
    @Transactional

        Payment p = paymentRepository.findById(cmd.getPaymentId())
                .orElseThrow(() -> new PaymentException(NOT_FOUND_PAYMENT));

        RefundType refundType = null;
        if(cmd.getChargeFee().compareTo(BigDecimal.ZERO) == 0){
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
    }
}
