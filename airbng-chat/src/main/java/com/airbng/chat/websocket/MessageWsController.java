package com.airbng.chat.websocket;

import com.airbng.chat.domain.Attachment;
import com.airbng.chat.domain.Message;
import com.airbng.chat.dto.chat.MessageDto;
import com.airbng.chat.dto.chat.SendTextRequest;
import com.airbng.chat.dto.ws.*;
import com.airbng.chat.repository.AttachmentRepository;
import com.airbng.chat.service.ConversationService;
import com.airbng.chat.service.InboxService;
import com.airbng.chat.service.MessageService;
import com.airbng.platform.security.principal.AirbngPrincipal;
import com.airbng.platform.util.S3Utils;
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
import org.springframework.security.core.context.SecurityContextHolder;
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

    private final S3Utils s3;
    private final AttachmentRepository attachmentRepository;

    private String sign(String key){ return s3.presignGetUrl(key, 60*60); } // 1시간 (원하면 24h)
    private String findKey(String attId){
        return attachmentRepository.findById(attId).map(Attachment::getKey).orElse(null);
    }

    // AirPrincipal로 대체
    private AirbngPrincipal currentUser(Principal principal) {
        if (principal instanceof Authentication a && a.getPrincipal() instanceof AirbngPrincipal cud) {
            return cud;
        }
        // SecurityContext에서 시도
        var ctxAuth = SecurityContextHolder.getContext().getAuthentication();
        if (ctxAuth != null && ctxAuth.getPrincipal() instanceof AirbngPrincipal cud2) {
            return cud2;
        }
        throw new AccessDeniedException("Unauthenticated");
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

        AirbngPrincipal me = currentUser(principal);
        conversationService.assertMember(convId, me.getId());

        log.info("[WS TEXT IN] from={} convId={} msgId={}", me.getId(), convId, payload.getMsgId());
        Message saved = messageService.sendText(
                convId, me.getId(), me.getNickname(),
                payload.getText(), payload.getMsgId()
        );
        var dto = MessageDto.from(saved, this::sign, this::findKey);
        log.info("[WS TEXT OUT] to=/topic/conversations.{} seq={} id={}", convId, saved.getSeq(), saved.getId());

        broker.convertAndSend("/topic/conversations." + convId, dto);

        broker.convertAndSendToUser(String.valueOf(me.getId()),
                "/queue/acks",
                new SendAck(saved.getMsgId(), saved.getSeq(),
                        saved.getSentAt() != null ? saved.getSentAt().toEpochMilli() : null));

        // ===== 인박스 힌트: 리스트 실시간 갱신 =====
        long peerId = conversationService.peerIdOf(convId, me.getId());
        Integer peerUnreadTotal = null;
        var peerInbox = inboxService.getOne(peerId, convId);
        if (peerInbox != null && peerInbox.getCachedUnread() != null) {
            peerUnreadTotal = peerInbox.getCachedUnread().intValue();
        }

        // 보낸 사람(나): 미확인은 0
        broker.convertAndSendToUser(String.valueOf(me.getId()),
                "/queue/inbox",
                InboxHint.builder()
                        .convId(convId)
                        .preview(saved.getText())
                        .sentAtMs(saved.getSentAt() != null ? saved.getSentAt().toEpochMilli() : null)
                        .senderId(me.getId())
                        .unreadTotal(0)
                        .build());

        // 상대방: 미확인 총합 포함
        broker.convertAndSendToUser(String.valueOf(peerId),
                "/queue/inbox",
                InboxHint.builder()
                        .convId(convId)
                        .preview(saved.getText())
                        .sentAtMs(saved.getSentAt() != null ? saved.getSentAt().toEpochMilli() : null)
                        .senderId(me.getId())
                        .unreadTotal(peerUnreadTotal) // null이어도 OK(클라가 +1만 해도 됨)
                        .build());
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

        AirbngPrincipal me = currentUser(principal);
        conversationService.assertMember(convId, me.getId());

        inboxService.markRead(me.getId(), convId, payload.getLastSeenSeq());

        long peer = conversationService.peerIdOf(convId, me.getId());
        broker.convertAndSendToUser(String.valueOf(peer),
                "/queue/read." + convId,
                payload.getLastSeenSeq());

        // ===== 인박스 힌트: 내 리스트 즉시 0 처리 =====
        broker.convertAndSendToUser(String.valueOf(me.getId()),
                "/queue/inbox",
                InboxHint.builder()
                        .convId(convId)
                        .unreadTotal(0)
                        .build());
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

        AirbngPrincipal me = currentUser(principal);
        conversationService.assertMember(convId, me.getId());

        log.info("[WS TYPING] from={} name={} convId={} typing={}",
                me.getId(),
                principal != null ? principal.getName() : null,
                convId,
                payload.isTyping());

        long peer = conversationService.peerIdOf(convId, me.getId());
        broker.convertAndSendToUser(String.valueOf(peer),
                "/queue/typing." + convId,
                new TypingEvent(me.getId(), payload.isTyping(), Instant.now()));
    }

    /* ================= Error Handling ================= */
    @MessageExceptionHandler
    public void handle(Exception ex, Principal principal) {          // ← 변경
        Long me = null;
        if (principal instanceof Authentication a && a.getPrincipal() instanceof AirbngPrincipal cud) {
            me = cud.getId();
        }
        log.error("[WS ERROR] user={} : {}", me, ex.getMessage(), ex);
        if (me != null) {
            broker.convertAndSendToUser(String.valueOf(me), "/queue/errors",
                    ex.getMessage() != null ? ex.getMessage() : "WebSocket error");
        }
    }
}
