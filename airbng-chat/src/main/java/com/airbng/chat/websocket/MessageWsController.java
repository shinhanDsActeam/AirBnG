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
import java.util.Map;

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
     * 클라 → 서버: /conversations/{convId}/read  (payload: { lastSeenSeq })
     * 서버 → 내 인박스 힌트: /user/queue/inbox          (unreadTotal=0)
     * 서버 → 방별 READ 이벤트(양쪽): /user/queue/read.{convId}  ({ userId, lastReadSeq })
     */
    @MessageMapping("/conversations/{convId}/read")
    public void wsMarkRead(@DestinationVariable String convId,
                           @Payload ReadPayload payload,
                           Principal principal) {

        AirbngPrincipal me = currentUser(principal);
        conversationService.assertMember(convId, me.getId());

        long lastSeenSeq = payload != null ? payload.getLastSeenSeq() : 0L;

        log.info("[READ IN] u={} conv={} lastSeenSeq={}", me.getId(), convId, lastSeenSeq);

        // 1) 서버 기준 읽음 반영 + unread 재계산
        inboxService.markRead(me.getId(), convId, lastSeenSeq);

        // 내 인박스 0 힌트
        broker.convertAndSendToUser(String.valueOf(me.getId()),
                "/queue/inbox", Map.of("convId", convId, "unreadTotal", 0));

        // READ 이벤트는 '객체'로 양쪽에게
        var readEvt = Map.of("userId", me.getId(), "lastReadSeq", lastSeenSeq);
        broker.convertAndSendToUser(String.valueOf(me.getId()), "/queue/read." + convId, readEvt);

        long peer = conversationService.peerIdOf(convId, me.getId());
        if (peer > 0) {
            broker.convertAndSendToUser(String.valueOf(peer), "/queue/read." + convId, readEvt);
        }

        // === LOG
        log.info("[READ OUT] conv={} -> self={}, peer={} payload={}", convId, me.getId(), peer, readEvt);
    }

    @MessageMapping("/conversations/{convId}/read-sync")
    public void wsReadSync(@DestinationVariable String convId,
                           @Payload ReadSyncRequest payload,
                           Principal principal) {

        var me = currentUser(principal);
        conversationService.assertMember(convId, me.getId());

        long meId   = me.getId();
        long peerId = conversationService.peerIdOf(convId, meId);

        long meLastRead   = 0L;
        long peerLastRead = 0L;

        var meInbox   = inboxService.getOne(meId, convId);
        var peerInbox = inboxService.getOne(peerId, convId);
        if (meInbox   != null && meInbox.getLastReadSeq()   != null) meLastRead   = meInbox.getLastReadSeq();
        if (peerInbox != null && peerInbox.getLastReadSeq() != null) peerLastRead = peerInbox.getLastReadSeq();

        // === LOG
        log.info("[READ-SYNC IN] u={} conv={} meLastRead={} peerLastRead={}",
                meId, convId, meLastRead, peerLastRead);

        // 1) 나에게: 상대의 읽은 위치(내 UI가 필요로 하는 값)
        broker.convertAndSendToUser(String.valueOf(meId),
                "/queue/read." + convId,
                java.util.Map.of("userId", peerId, "lastReadSeq", peerLastRead));

        // 2) 상대에게도: 나의 읽은 위치(멱등 동기화)
        if (peerId > 0) {
            broker.convertAndSendToUser(String.valueOf(peerId),
                    "/queue/read." + convId,
                    java.util.Map.of("userId", meId, "lastReadSeq", meLastRead));
        }

        // 3) 선택: 방에 들어온 나의 인박스 뱃지는 바로 0
        broker.convertAndSendToUser(String.valueOf(meId),
                "/queue/inbox",
                java.util.Map.of("convId", convId, "unreadTotal", 0));

        // === LOG
        log.info("[READ-SYNC OUT] conv={} to={} (peerRead={}), toPeer={} (meRead={})",
                convId, meId, peerLastRead, peerId, meLastRead);
    }

    /**
     * 타이핑 표시(선택)
     * 클라 → 서버: /conversations/{convId}/typing
     * 서버 → 상대 유저: /user/queue/typing.{convId}
     */
    @MessageMapping("/conversations/{convId}/typing")
    public void wsTyping(@DestinationVariable String convId,
                         @Payload TypingPayload payload,
                         Principal principal) {

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
    public void handle(Exception ex, Principal principal) {
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
