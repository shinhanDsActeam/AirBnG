package com.airbng.chat.controller;

import com.airbng.api.consumer.MemberApi;
import com.airbng.api.consumer.dto.view.MemberCardView;
import com.airbng.chat.domain.Inbox;
import com.airbng.chat.dto.chat.InboxItemDto;
import com.airbng.chat.dto.chat.LastMessageDto;
import com.airbng.chat.service.ConversationService;
import com.airbng.platform.common.response.BaseResponse;
import com.airbng.platform.security.principal.AirbngPrincipal;
import com.airbng.chat.service.InboxService;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/chat/inbox")
@RequiredArgsConstructor
@Validated
@Slf4j
public class InboxController {

    private final InboxService inboxService;
    private final ConversationService conversationService;
    private final MemberApi memberApi;
    private final SimpMessagingTemplate broker;

    /**
     * 내 인박스 목록 조회 (최신순)
     * GET /chat/inbox?page=0&size=30
     */
    @GetMapping
    @PreAuthorize("hasAnyAuthority('USER')")
    public BaseResponse<List<InboxItemDto>> list(@RequestParam(defaultValue = "0") @Min(0) int page,
                                                 @RequestParam(defaultValue = "30") @Min(1) int size,
                                                 Authentication auth) {
        long me = ((AirbngPrincipal) auth.getPrincipal()).getId();
        List<Inbox> rows = inboxService.getInbox(me, PageRequest.of(page, size));

        // 1) peerIds 배치 조회
        Set<Long> peerIds = rows.stream()
                .map(Inbox::getPeerId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        Map<Long, MemberCardView> cardMap = memberApi.findCardsByIds(peerIds).stream()
                .collect(Collectors.toMap(MemberCardView::getMemberId, v -> v));

        // 2) Inbox → InboxItemDto 매핑 (denorm 값 우선, 없으면 card로 보강)
        List<InboxItemDto> dto = rows.stream().map(in -> {
            MemberCardView card = cardMap.get(in.getPeerId());
            String name = (in.getPeerName() != null && !in.getPeerName().isBlank())
                    ? in.getPeerName()
                    : (card != null ? card.getName() : null);

            return new InboxItemDto(
                    in.getConvId(),
                    in.getPeerId(),
                    name,
                    card != null ? card.getNickname() : null,
                    card != null ? card.getImageUrl() : null,
                    in.getLastMessage() != null ? LastMessageDto.from(in.getLastMessage()) : null,
                    in.getLastMessageAt() != null ? in.getLastMessageAt().toEpochMilli() : null,
                    in.getCachedUnread()
            );
        }).toList();

        return new BaseResponse<>(dto);
    }

    /**
     * 읽음 처리(lastReadSeq = max(old, lastSeenSeq) & cachedUnread 재계산)
     * POST /chat/inbox/{convId}/read?lastSeenSeq=123
     */
    @PostMapping("/{convId}/read")
    @PreAuthorize("hasAnyAuthority('USER')")
    public BaseResponse<Void> markRead(@PathVariable String convId,
                                       @RequestParam @Min(0) long lastSeenSeq,
                                       Authentication auth) {
        long me = ((AirbngPrincipal) auth.getPrincipal()).getId();
        conversationService.assertMember(convId, me);

        inboxService.markRead(me, convId, lastSeenSeq);

        // 1) 내 인박스 뱃지 0
        broker.convertAndSendToUser(String.valueOf(me),
                "/queue/inbox", Map.of("convId", convId, "unreadTotal", 0));

        // 2) READ 이벤트(WS와 동일 포맷) → 나 + 상대
        var readEvt = Map.of("userId", me, "lastReadSeq", lastSeenSeq);
        broker.convertAndSendToUser(String.valueOf(me),  "/queue/read." + convId, readEvt);

        long peer = conversationService.peerIdOf(convId, me);
        if (peer > 0) {
            broker.convertAndSendToUser(String.valueOf(peer), "/queue/read." + convId, readEvt);
        }

        log.info("[HTTP READ OUT] conv={} self={} peer={} evt={}", convId, me, peer, readEvt);
        return new BaseResponse<>((Void) null);
    }

    /**
     * 모두 읽음 처리(대화방 highestSeq까지)
     * POST /chat/inbox/{convId}/read-all
     */
    @PostMapping("/{convId}/read-all")
    @PreAuthorize("hasAnyAuthority('USER')")
    public BaseResponse<Void> markAllRead(@PathVariable String convId,
                                          Authentication auth) {
        long me = ((AirbngPrincipal) auth.getPrincipal()).getId();
        conversationService.assertMember(convId, me);

        var conv = conversationService.findById(convId);
        long targetSeq = (conv != null && conv.getHighestSeq() != null) ? conv.getHighestSeq() : 0L;

        inboxService.markRead(me, convId, targetSeq);
        return new BaseResponse<>((Void) null);
    }

    @GetMapping("/{convId}")
    @PreAuthorize("hasAnyAuthority('USER')")
    public BaseResponse<InboxItemDto> getOne(@PathVariable String convId, Authentication auth) {
        long me = ((AirbngPrincipal) auth.getPrincipal()).getId();
        conversationService.assertMember(convId, me);

        Inbox in = inboxService.getOne(me, convId);
        if (in == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "inbox not found");
        }

        // 보강용 card 한 건 조회
        MemberCardView card = null;
        var cards = memberApi.findCardsByIds(List.of(in.getPeerId()));
        if (!cards.isEmpty()) card = cards.get(0);

        String name = in.getPeerName();
        if ((name == null || name.isBlank()) && card != null) name = card.getName();

        String nickname = (card != null) ? card.getNickname() : null;
        String profileUrl = (card != null) ? card.getImageUrl() : null;

        InboxItemDto dto = new InboxItemDto(
                in.getConvId(),
                in.getPeerId(),
                name,
                nickname,
                profileUrl,
                in.getLastMessage() != null ? LastMessageDto.from(in.getLastMessage()) : null,
                in.getLastMessageAt() != null ? in.getLastMessageAt().toEpochMilli() : null,
                in.getCachedUnread()
        );
        return new BaseResponse<>(dto);
    }

    @GetMapping("/unread-total")
    @PreAuthorize("hasAnyAuthority('USER')")
    public BaseResponse<Integer> unreadTotal(Authentication auth) {
        long me = ((AirbngPrincipal) auth.getPrincipal()).getId();
        return new BaseResponse<>(inboxService.totalUnread(me));
    }
}
