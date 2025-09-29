package com.airbng.api.consumer.event;

public record ReservationCancelledEvent(
        Long reservationId,
        Long refundId,
        Long dropperId,
        Long keeperId
) {
    public static ReservationCancelledEvent of(Long reservationId, Long refundId, Long dropperId, Long keeperId) {
        return new ReservationCancelledEvent(reservationId, refundId, dropperId, keeperId);
    }
}