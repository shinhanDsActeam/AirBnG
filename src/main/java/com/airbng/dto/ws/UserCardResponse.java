package com.airbng.dto.ws;

import lombok.Builder;

@Builder
public record UserCardResponse(
        Long id,
        String name,
        String nickname,
        String imageUrl,
        String state,    // "online" | "offline" (여기선 online 고정)
        Long since       // 옵션: presence 기준 시각 (없으면 null)
) {}
