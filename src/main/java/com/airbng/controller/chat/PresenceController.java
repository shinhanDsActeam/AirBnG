package com.airbng.controller.chat;

import com.airbng.common.response.BaseResponse;
import com.airbng.security.domain.CustomUserDetails;
import com.airbng.service.chat.PresenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/chat/presence")
@RequiredArgsConstructor
public class PresenceController {

    private final PresenceService presenceService;

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
        long me = ((CustomUserDetails) auth.getPrincipal()).getId();
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
}
