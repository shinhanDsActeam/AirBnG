package com.airbng.chat.controller;

import com.airbng.chat.dto.ws.UserCardResponse;
import com.airbng.chat.service.DirectoryService;
import com.airbng.chat.service.PresenceService;
import com.airbng.platform.common.response.BaseResponse;
import com.airbng.platform.security.principal.AirbngPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.airbng.platform.common.response.status.BaseResponseStatus.SUCCESS;

@RestController
@RequestMapping("/chat/presence")
@RequiredArgsConstructor
public class PresenceController {

    private final PresenceService presenceService;
    private final DirectoryService directoryService;

    /** 단건 온라인 여부 */
    @GetMapping("/{userId}")
    @PreAuthorize("hasAnyAuthority('USER')")
    public BaseResponse<Boolean> isOnline(@PathVariable long userId) {
        return new BaseResponse<>(presenceService.isOnline(userId));
    }

    /** 내 온라인 여부(세션 수 포함) */
    @GetMapping("/me")
    @PreAuthorize("hasAnyAuthority('USER')")
    public BaseResponse<Map<String, Object>> me(Authentication auth) {
        long me = ((AirbngPrincipal) auth.getPrincipal()).getId();
        boolean online = presenceService.isOnline(me);
        long sessions = presenceService.onlineSessionCount(me);
        return new BaseResponse<>(Map.of("userId", me, "online", online, "sessions", sessions));
    }

    /** 복수 온라인 여부 쿼리: /chat/presence?ids=1,2,3 */
    @GetMapping
    @PreAuthorize("hasAnyAuthority('USER')")
    public BaseResponse<Map<Long, Boolean>> isOnlineBulk(@RequestParam List<Long> ids) {
        return new BaseResponse<>(presenceService.isOnlineBulk(ids));
    }

    /**
     * 온라인 사용자 카드 목록 (무한스크롤)
     * ex) GET /chat/presence/online-users?offset=0&size=20&q=&includeMe=true
     * 응답: { items: UserCardResponse[], nextOffset: number, hasMore: boolean }
     */
    @GetMapping("/online-users")
    @PreAuthorize("hasAnyAuthority('USER')")
    public BaseResponse<Map<String, Object>> onlineUsers(
            @RequestParam(defaultValue = "0")  int offset,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false)    String q,
            @RequestParam(defaultValue = "false") boolean includeMe,
            Authentication auth
    ) {
        long me = ((AirbngPrincipal) auth.getPrincipal()).getId();

        // 1) 원자료(Page) 가져오기 (+1로 더보기 판단)
        List<Long> raw = presenceService.onlineUserIdsPage(offset, size + 1);
        boolean hasMore = raw.size() > size;
        int advance = Math.min(raw.size(), size); // 다음 offset 계산에 사용

        // 2) 페이지 절단
        List<Long> page = raw.stream().limit(size).collect(Collectors.toList());

        // includeMe=false면 본인 제외
        if (!includeMe) page = page.stream().filter(id -> id != me).collect(Collectors.toList());

        // 3) 카드 조회
        List<UserCardResponse> cards = page.isEmpty()
                ? List.of()
                : directoryService.getUserCards(page);

        // 4) 검색어 필터 (표시만 필터, 페이징 오프셋은 raw 기준 유지)
        if (q != null && !q.isBlank()) {
            String s = q.toLowerCase();
            cards = cards.stream().filter(c ->
                    (c.name() != null     && c.name().toLowerCase().contains(s)) ||
                            (c.nickname() != null && c.nickname().toLowerCase().contains(s))
            ).collect(Collectors.toList());
        }

        // 5) 원래 id 순서 유지
        Map<Long,Integer> order = new HashMap<>();
        for (int i = 0; i < page.size(); i++) order.put(page.get(i), i);
        cards = cards.stream()
                .sorted(Comparator.comparingInt(c -> order.getOrDefault(c.id(), Integer.MAX_VALUE)))
                .collect(Collectors.toList());

        Map<String, Object> payload = Map.of(
                "items", cards,
                "nextOffset", offset + advance,
                "hasMore", hasMore
        );
        return new BaseResponse<>(payload);
    }

    @PostMapping("/ping")
    @PreAuthorize("hasAnyAuthority('USER')")
    public BaseResponse<Void> ping(Authentication auth) {
        long me = ((AirbngPrincipal) auth.getPrincipal()).getId();
        presenceService.touch(me);
        return new BaseResponse<>(SUCCESS);
    }
}
