package com.airbng.websocket;

import com.airbng.domain.chat.Message;
import com.airbng.dto.chat.SendTextRequest;
import com.airbng.dto.ws.ReadPayload;
import com.airbng.dto.ws.SendAck;
import com.airbng.dto.ws.TypingEvent;
import com.airbng.dto.ws.TypingPayload;
import com.airbng.security.domain.CustomUserDetails;
import com.airbng.service.chat.ConversationService;
import com.airbng.service.chat.InboxService;
import com.airbng.service.chat.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.time.Instant;

@Slf4j
@Controller
@RequiredArgsConstructor
public class MessageWsController {

    private final MessageService messageService;
    private final ConversationService conversationService;
    private final InboxService inboxService;
    private final SimpMessagingTemplate broker;

    private CustomUserDetails currentUser(Principal principal) {
        if (!(principal instanceof Authentication a)) throw new AccessDeniedException("Unauthenticated");
        Object p = a.getPrincipal();
        if (!(p instanceof CustomUserDetails cud)) throw new AccessDeniedException("Unauthenticated");
        return cud;
    }

    /**
     * 텍스트 전송 (멱등 msgId 필수)
     * 클라 → 서버: /conversations/{convId}/text
     * 서버 → 구독자: /topic/conversations.{convId}
     * 서버 → 보낸 유저(ACK): /user/queue/acks
     */
    @MessageMapping("/conversations/{convId}/text")
    public void wsSendText(@DestinationVariable String convId,
                           @Payload SendTextRequest payload,
                           Principal principal,                      // ← 변경
                           SimpMessageHeaderAccessor headers) {

        CustomUserDetails me = currentUser(principal);
        conversationService.assertMember(convId, me.getId());

        Message saved = messageService.sendText(
                convId, me.getId(), me.getNickname(),
                payload.getText(), payload.getMsgId()
        );

        broker.convertAndSend("/topic/conversations." + convId, saved);

        broker.convertAndSendToUser(String.valueOf(me.getId()),
                "/queue/acks",
                new SendAck(saved.getMsgId(), saved.getSeq(), saved.getSentAt()));
    }

    /**
     * 읽음 처리
     * 클라 → 서버: /conversations/{convId}/read
     * 서버 → 상대 유저: /user/queue/read.{convId}
     */
    @MessageMapping("/conversations/{convId}/read")
    public void wsMarkRead(@DestinationVariable String convId,
                           @Payload ReadPayload payload,
                           Principal principal) {                     // ← 변경

        CustomUserDetails me = currentUser(principal);
        conversationService.assertMember(convId, me.getId());

        inboxService.markRead(me.getId(), convId, payload.getLastSeenSeq());

        long peer = conversationService.peerIdOf(convId, me.getId());
        broker.convertAndSendToUser(String.valueOf(peer),
                "/queue/read." + convId,
                payload.getLastSeenSeq());
    }

    /**
     * 타이핑 표시(선택)
     * 클라 → 서버: /conversations/{convId}/typing
     * 서버 → 상대 유저: /user/queue/typing.{convId}
     */
    @MessageMapping("/conversations/{convId}/typing")
    public void wsTyping(@DestinationVariable String convId,
                         @Payload TypingPayload payload,
                         Principal principal) {                      // ← 변경

        CustomUserDetails me = currentUser(principal);
        conversationService.assertMember(convId, me.getId());

        long peer = conversationService.peerIdOf(convId, me.getId());
        broker.convertAndSendToUser(String.valueOf(peer),
                "/queue/typing." + convId,
                new TypingEvent(me.getId(), payload.isTyping(), Instant.now()));
    }

    /* ================= Error Handling ================= */
    @MessageExceptionHandler
    public void handle(Exception ex, Principal principal) {          // ← 변경
        Long me = null;
        if (principal instanceof Authentication a && a.getPrincipal() instanceof CustomUserDetails cud) {
            me = cud.getId();
        }
        log.error("[WS ERROR] user={} : {}", me, ex.getMessage(), ex);
        if (me != null) {
            broker.convertAndSendToUser(String.valueOf(me), "/queue/errors",
                    ex.getMessage() != null ? ex.getMessage() : "WebSocket error");
        }
    }
}
