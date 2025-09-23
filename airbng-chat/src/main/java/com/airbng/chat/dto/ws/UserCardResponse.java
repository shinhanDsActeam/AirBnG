package com.airbng.chat.dto.ws;

import lombok.Builder;

@Builder
public record UserCardResponse(
        Long id,
        String name,
        String nickname,
        String imageUrl,
        String state,       // "online" | "offline" (서버에서 일관 생성)
        Long since,         // epochSec (최근 활동/접속 시각)
        Boolean online,     // 편의 플래그
        Integer sessions,   // 현재 접속 세션 수 (없으면 null)
        String displayName  // nickname 우선, 없으면 name
) {}
