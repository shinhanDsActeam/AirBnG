package com.airbng.chat.listener;

import com.airbng.api.consumer.ReservationApi;
import com.airbng.api.consumer.dto.view.ReservationCardPayload;
import com.airbng.api.consumer.event.ReservationCancelledEvent;
import com.airbng.api.pay.RefundReadApi;
import com.airbng.api.pay.dto.view.RefundCardPayload;
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
    private final SimpMessagingTemplate messaging; // STOMP 사용 시
    private final ConversationService conversationService;
    private final MessageService messageService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onCancelled(ReservationCancelledEvent e) {
        try {
            ReservationCardPayload r = reservationApi.getCardPayload(e.reservationId());
            RefundCardPayload refund = refundReadApi.getCardPayload(e.refundId());

            // 1) 드로퍼-키퍼 1:1 대화 보장(or 생성)
            String convId = conversationService.ensureByPeer(e.dropperId(), e.keeperId());

            // 2) 카드 페이로드 구성
            var payload = CancelledWithRefundCardDto.of(
                    convId, r, refund,
                    "예약이 취소되었습니다.",
                    "환불이 접수되었어요."
            );

            // 4) 대화 토픽으로 브로드캐스트 → 방이 열려 있지 않아도 다음 입장 시 히스토리로 확인 가능
            messaging.convertAndSend("/topic/conversations/" + convId + "/cards", payload);

            // 5) (선택) 개인 큐로도 즉시 Push — 이미 화면이 열려있는 유저에게 즉시 보여주고 싶다면 유지
            messaging.convertAndSendToUser(String.valueOf(e.dropperId()), "/queue/chat.cards", payload);
            messaging.convertAndSendToUser(String.valueOf(e.keeperId()), "/queue/chat.cards", payload);

            log.info("[chat-card] CancelledWithRefundCard broadcasted. conv={}, reservation={}, refund={}",
                    convId, e.reservationId(), e.refundId());
        } catch (Exception ex) {
            log.error("failed to publish CancelledWithRefundCard", ex);
        }
    }

}
