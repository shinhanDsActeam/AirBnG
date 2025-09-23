package com.airbng.chat.listener;

import com.airbng.api.consumer.event.ReservationCreatedEvent;
import com.airbng.api.consumer.ReservationApi; // 카드 페이로드 조회용
import com.airbng.chat.service.ConversationService;
import com.airbng.chat.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.transaction.event.TransactionPhase;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReservationCreatedListener {

    private final ReservationApi reservationApi;       // 카드 페이로드 조회
    private final ConversationService conversationService;
    private final MessageService messageService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onReservationCreated(ReservationCreatedEvent e) {
        // 1) 카드 페이로드 조회 (consumer 내부 서비스 직접 호출)
        var payload = reservationApi.getCardPayload(e.reservationId());

        // 2) convId 계산
        var convId = conversationService.makeConvId(payload.dropperId(), payload.keeperId());

        // 3) 카드 메시지 저장/브로드캐스트
        messageService.sendReservationCard(
                convId,
                payload.dropperId(),      // 보낸 사람을 dropper로 잡을지 시스템으로 잡을지는 정책에 맞춰 조정
                payload.dropperName(),
                payload,
                e.msgId()
        );

        log.info("Pushed reservation card to chat. reservationId={}, convId={}", e.reservationId(), convId);
    }
}