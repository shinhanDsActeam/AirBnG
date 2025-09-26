package com.airbng.chat.listener;

import com.airbng.api.consumer.ReservationApi;
import com.airbng.api.consumer.dto.view.ReservationCardPayload;
import com.airbng.api.consumer.event.ReservationCancelledEvent;
import com.airbng.api.pay.RefundReadApi;
import com.airbng.api.pay.dto.RefundStatus;
import com.airbng.api.pay.dto.command.RefundType;
import com.airbng.api.pay.dto.view.RefundCardPayload;
import com.airbng.chat.domain.Message;
import com.airbng.chat.dto.chat.CancelledWithRefundCardDto;
import com.airbng.chat.dto.chat.MessageDto;
import com.airbng.chat.service.ConversationService;
import com.airbng.chat.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReservationCancelledListener {

    private final ReservationApi reservationApi;
    private final RefundReadApi refundReadApi;
    private final SimpMessagingTemplate messaging;
    private final ConversationService conversationService;
    private final MessageService messageService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onCancelled(ReservationCancelledEvent e) {
        final String dedupKey = "reservation:" + e.reservationId() + ":cancelled";

        try {
            String convId = conversationService.ensureByPeer(e.dropperId(), e.keeperId());

            ReservationCardPayload r = reservationApi.getCardPayload(e.reservationId());

            RefundCardPayload refund;
            try {
                refund = refundReadApi.getCardPayload(e.refundId());
            } catch (Exception ex) {
                log.warn("Refund payload fetch failed: refundId={}", e.refundId(), ex);
                refund = new RefundCardPayload(
                        e.refundId(),
                        e.reservationId(),
                        null, null, null, null,
                        0L, 0L,
                        RefundType.FULL,
                        null,
                        RefundStatus.PENDING
                );
            }

            // 저장(멱등) — 잘못 넘기던 인자 수정: r, refund, dedupKey
            Message saved = messageService.saveReservationCancelledCardIfAbsent(
                    convId, r, refund, dedupKey
            );

            // 브로드캐스트는 MessageDto로
            MessageDto dto = MessageDto.from(saved, k -> null, k -> null);
//            messaging.convertAndSend("/topic/conversations/" + convId + "/cards", dto);
            messaging.convertAndSend("/topic/conversations." + convId, dto);
            messaging.convertAndSendToUser(String.valueOf(e.dropperId()), "/queue/chat.cards", dto);
            messaging.convertAndSendToUser(String.valueOf(e.keeperId()), "/queue/chat.cards", dto);

            log.info("[chat-card] saved & broadcast: conv={}, reservation={}, refund={}",
                    convId, e.reservationId(), e.refundId());

        } catch (Exception ex) {
            log.error("failed to persist/broadcast CancelledWithRefundCard", ex);
        }
    }
}
