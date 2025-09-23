package com.airbng.api.consumer.dto.view;

import com.airbng.api.consumer.dto.common.ReservationStatus;

import java.time.LocalDateTime;

public record ReservationDecisionResult(
        Long reservationId,
        ReservationStatus newStatus,         // CONFIRMED | REJECTED
        Long decidedBy,           // actorId
        LocalDateTime decidedAt
) {}
