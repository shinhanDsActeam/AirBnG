package com.airbng.api.consumer.dto.command;

public record ReservationDecisionCommand(
        Long reservationId,
        Long actorId,
        boolean approve,
        String reason
) {}
