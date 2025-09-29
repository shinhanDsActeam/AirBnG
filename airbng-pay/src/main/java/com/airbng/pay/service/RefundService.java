package com.airbng.pay.service;

import com.airbng.api.pay.dto.command.RefundDecisionCommand;
import com.airbng.api.pay.dto.command.RefundRequestCommand;
import com.airbng.api.pay.dto.view.RefundDecisionResult;
import com.airbng.pay.domain.Refund;

public interface RefundService {
    /** 환불요청 멱등 생성(또는 기존 반환) + 필요 시 즉시 처리 */
    Refund requestRefund(RefundRequestCommand cmd);

    /** keeper 승인/거절 결정 + 필요 시 즉시 처리 */
    RefundDecisionResult decide(RefundDecisionCommand cmd);
}
