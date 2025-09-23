package com.airbng.api.pay.dto.command;

public record RefundRequestCommand(
        String idemKey,        // 멱등키
        Long reservationId,
        Long paymentId,
        Long actorId,          // dropper
        RefundMode mode,
        String reason
) {}