package com.airbng.api.chat;

public interface ChatApi {
    void pushReservationCard(Long reservationId, Long dropperId, Long keeperId, String msgId);
}