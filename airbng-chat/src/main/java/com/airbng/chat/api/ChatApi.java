package com.airbng.chat.api;

public interface ChatApi {
    void pushReservationCard(Long reservationId, Long dropperId, Long keeperId, String msgId);
}
