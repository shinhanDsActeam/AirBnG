package com.airbng.api.consumer.event;

import java.io.Serializable;
import java.util.UUID;

public record ReservationCreatedEvent(
        Long reservationId,
        Long dropperId,
        Long keeperId,
        String msgId // 멱등키
) implements Serializable {
    public static ReservationCreatedEvent of(Long reservationId, Long dropperId, Long keeperId) {
        return new ReservationCreatedEvent(reservationId, dropperId, keeperId, UUID.randomUUID().toString());
    }
}