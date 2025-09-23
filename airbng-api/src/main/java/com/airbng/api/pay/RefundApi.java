package com.airbng.api.pay;

import com.airbng.api.pay.dto.command.RefundDecisionCommand;
import com.airbng.api.pay.dto.command.RefundRequestCommand;
import com.airbng.api.pay.dto.view.RefundCardPayload;
import com.airbng.api.pay.dto.view.RefundDecisionResult;

public interface RefundApi {
    /** dropper가 환불 요청(혹은 자동 전액환불 트리거) */
    RefundCardPayload requestRefund(RefundRequestCommand cmd);

    /** keeper가 환불 승인/거절 결정 */
    RefundDecisionResult decide(RefundDecisionCommand cmd);
}
