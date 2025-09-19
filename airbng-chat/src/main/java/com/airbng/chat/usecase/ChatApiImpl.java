package com.airbng.chat.usecase;


import com.airbng.api.consumer.ReservationApi;
import com.airbng.api.consumer.dto.view.ReservationCardPayload;
import com.airbng.chat.api.ChatApi;
import com.airbng.chat.dto.chat.MessageDto;
import com.airbng.chat.service.ConversationService;
import com.airbng.chat.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
class ChatApiImpl implements ChatApi {

    private final ConversationService conversationService;
    private final MessageService messageService;
    private final SimpMessagingTemplate messagingTemplate;

    private final ReservationApi reservationApi;

    @Override
    public void pushReservationCard(Long reservationId, Long dropperId, Long keeperId, String msgId) {
        // 1) 카드 페이로드 조회
        ReservationCardPayload payload = reservationApi.getCardPayload(reservationId);

        // 2) convId 식별 & 메시지 저장
        var convId = conversationService.makeConvId(dropperId, keeperId);
        var saved = messageService.sendReservationCard(
                convId,
                dropperId, payload.dropperName(), // 보낸사람 표기 정책에 맞게
                payload,
                msgId
        );

        // 3) 실시간 방송
        var dto = MessageDto.from(saved, null, null);
        messagingTemplate.convertAndSend("/topic/conversations." + convId, dto);
    }
}