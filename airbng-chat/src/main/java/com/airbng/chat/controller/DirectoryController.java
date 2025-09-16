package com.airbng.chat.controller;

import com.airbng.chat.dto.ws.UserCardResponse;
import com.airbng.chat.service.DirectoryService;
import com.airbng.platform.common.response.BaseResponse;
import com.airbng.platform.security.principal.AirbngPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/chat/directory")
@RequiredArgsConstructor
public class DirectoryController {

    private final DirectoryService directoryService;

    /** 여러 ID로 카드 조회(오프라인 포함, 입력 순서 유지) */
    @GetMapping("/users")
    @PreAuthorize("hasAnyAuthority('USER')")
    public BaseResponse<List<UserCardResponse>> getUsers(@RequestParam List<Long> ids) {
        return new BaseResponse<>(directoryService.getUserCards(ids));
    }

    /** 단건 조회 */
    @GetMapping("/users/{userId}")
    @PreAuthorize("hasAnyAuthority('USER')")
    public BaseResponse<UserCardResponse> getUser(@PathVariable long userId) {
        return new BaseResponse<>(directoryService.getUserCard(userId));
    }

    /** 닉네임 정확 일치 검색(오프라인 포함) */
    @GetMapping("/lookup")
    @PreAuthorize("hasAnyAuthority('USER')")
    public BaseResponse<UserCardResponse> lookupByNickname(@RequestParam String nickname) {
        var card = directoryService.findByNickname(nickname);
        if (card == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다.");
        }
        return new BaseResponse<>(card);
    }

    /** 부분 검색 제안 (닉네임/이름 substring) */
    @GetMapping("/suggest")
    @PreAuthorize("hasAnyAuthority('USER')")
    public BaseResponse<List<UserCardResponse>> suggest(
            @RequestParam(name = "q") String q,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "false") boolean includeMe,
            Authentication auth
    ) {
        Long me = ((AirbngPrincipal) auth.getPrincipal()).getId();
        Long exclude = includeMe ? null : me;
        return new BaseResponse<>(directoryService.suggest(q, limit, exclude));
    }
}
