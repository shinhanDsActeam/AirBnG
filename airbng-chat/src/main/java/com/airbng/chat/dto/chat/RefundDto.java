package com.airbng.chat.dto.chat;

import java.time.Instant;

public record RefundDto(
        Long refundId,
        Long reservationId,
        Long paymentId,
        Long payerId,
        Long keeperId,
        Long lockerId,
        long amount,
        long feeToKeeper,
        String reason,
        Instant requestedAt,
        String status,
        boolean canDecide
) {}