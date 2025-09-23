package com.airbng.api.pay.dto.command;

public record RefundDecisionCommand(
        Long refundId,
        Long actorId,           // keeper
        boolean approve,
        String reason
) {}