package com.airbng.api.pay;

import com.airbng.api.pay.dto.view.RefundCardPayload;

public interface RefundReadApi {
    RefundCardPayload getCardPayload(Long refundId);
}