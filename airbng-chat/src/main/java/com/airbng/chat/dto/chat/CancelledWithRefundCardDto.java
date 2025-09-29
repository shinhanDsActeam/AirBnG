package com.airbng.chat.dto.chat;

import com.airbng.api.consumer.dto.view.ReservationCardPayload;
import com.airbng.api.pay.dto.view.RefundCardPayload;

/** 채팅에 뿌릴 "취소+환불" 카드 페이로드 */
public record CancelledWithRefundCardDto(
        String convId,
        ReservationCardPayload reservation,
        RefundCardPayload refund,
        String title,
        String subtitle
) {
    public static CancelledWithRefundCardDto of(
            String convId,
            ReservationCardPayload r,
            RefundCardPayload ref,
            String title,
            String subtitle
    ) {
        return new CancelledWithRefundCardDto(convId, r, ref, title, subtitle);
    }
}