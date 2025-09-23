package com.airbng.pay.usecase;

import com.airbng.api.pay.RefundApi;
import com.airbng.api.pay.dto.command.RefundDecisionCommand;
import com.airbng.api.pay.dto.command.RefundRequestCommand;
import com.airbng.api.pay.dto.view.RefundCardPayload;
import com.airbng.api.pay.dto.view.RefundDecisionResult;
import com.airbng.api.pay.dto.RefundStatus; // api enum
import com.airbng.pay.domain.Refund;
import com.airbng.pay.service.RefundService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RefundApiImpl implements RefundApi {

    private final RefundService refundService;

    @Override
    @Transactional
    public RefundCardPayload requestRefund(RefundRequestCommand cmd) {
        Refund r = refundService.requestRefund(cmd);
        return toCardPayload(r, cmd.actorId());
    }

    @Override
    @Transactional
    public RefundDecisionResult decide(RefundDecisionCommand cmd) {
        return refundService.decide(cmd);
    }

    private RefundCardPayload toCardPayload(Refund r, Long actorId) {
        return new RefundCardPayload(
                r.getRefundId(),
                r.getReservationId(),
                r.getPayment().getPaymentId(),
                r.getLockerId(),
                r.getPayerId(),                                 // dropper
                r.getPayeeId(),                                 // keeper
                r.getRefundAmount().longValue(),
                r.getChargeFee().longValue(),
                r.getRefundReason(),
                r.getRequestedAt().atZone(ZoneId.systemDefault()).toInstant(),
                RefundStatus.valueOf(r.getRefundStatus().name()),
                actorId != null && actorId.equals(r.getPayeeId())
        );
    }
}
