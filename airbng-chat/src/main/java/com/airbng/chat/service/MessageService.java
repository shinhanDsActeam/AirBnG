package com.airbng.chat.service;

import com.airbng.api.consumer.dto.view.ReservationCardPayload;
import com.airbng.chat.domain.Message;

import java.util.List;

public interface MessageService {

    /** 텍스트 전송(멱등: msgId) */
    Message sendText(String convId, long senderId, String senderName, String text, String msgId);

    // 예약 카드 전송
    Message decideReservation(String convId, long actorId, Long reservationId, boolean approve, String reason);

    Message sendReservationCard(String convId,
                                long senderId,
                                String senderName,
                                ReservationCardPayload payload,
                                String msgId);

    /** 메시지 페이징 (beforeSeq 미지정 시 최신부터) */
    List<Message> getMessages(String convId, Long beforeSeq, int size);
}
