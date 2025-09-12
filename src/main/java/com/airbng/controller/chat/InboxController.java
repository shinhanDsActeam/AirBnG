package com.airbng.controller.chat;

import com.airbng.common.response.BaseResponse;
import com.airbng.domain.chat.Inbox;
import com.airbng.security.domain.CustomUserDetails;
import com.airbng.service.chat.ConversationService;
import com.airbng.service.chat.InboxService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/chat/inbox")
@RequiredArgsConstructor
@Validated
public class InboxController {

    private final InboxService inboxService;
    private final ConversationService conversationService;

    /**
     * 내 인박스 목록 조회 (최신순)
     * GET /chat/inbox?page=0&size=30
     */
    @GetMapping
    @PreAuthorize("hasAnyAuthority('USER')")
    public BaseResponse<List<Inbox>> list(@RequestParam(defaultValue = "0") @Min(0) int page,
                                          @RequestParam(defaultValue = "30") @Min(1) int size,
                                          Authentication auth) {
        long me = ((CustomUserDetails) auth.getPrincipal()).getId();
        return new BaseResponse<>(inboxService.getInbox(me, PageRequest.of(page, size)));
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
        long me = ((CustomUserDetails) auth.getPrincipal()).getId();
        // 안전장치: 내가 속한 대화방만 허용
        conversationService.assertMember(convId, me);

        inboxService.markRead(me, convId, lastSeenSeq);
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
        long me = ((CustomUserDetails) auth.getPrincipal()).getId();
        conversationService.assertMember(convId, me);

        var conv = conversationService.findById(convId);
        long targetSeq = (conv != null && conv.getHighestSeq() != null) ? conv.getHighestSeq() : 0L;

        inboxService.markRead(me, convId, targetSeq);
        return new BaseResponse<>((Void) null);
    }

    @GetMapping("/{convId}")
    @PreAuthorize("hasAnyAuthority('USER')")
    public BaseResponse<Inbox> getOne(@PathVariable String convId, Authentication auth) {
        long me = ((CustomUserDetails) auth.getPrincipal()).getId();
        // 내가 속한 방만 허용
        conversationService.assertMember(convId, me);

        Inbox inbox = inboxService.getOne(me, convId);
        if (inbox == null) {
            // 처음 대화 시작 전일 수 있으니 404로 명확히 응답
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.NOT_FOUND, "inbox not found");
        }
        return new BaseResponse<>(inbox);
    }

    @GetMapping("/unread-total")
    @PreAuthorize("hasAnyAuthority('USER')")
    public BaseResponse<Integer> unreadTotal(Authentication auth) {
        long me = ((CustomUserDetails) auth.getPrincipal()).getId();
        return new BaseResponse<>(inboxService.totalUnread(me));
    }
}
