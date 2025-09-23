package com.airbng.chat.dto.chat;

import com.airbng.chat.domain.model.LastMessage;

public record LastMessageDto(
        String messageId,
        Long   senderId,
        String type,
        String preview,
        Long   sentAtMs      // epoch ms
) {
    public static LastMessageDto from(LastMessage lm) {
        return new LastMessageDto(
                lm.getMessageId(),
                lm.getSenderId(),
                lm.getType(),
                lm.getPreview(),
                lm.getSentAt() != null ? lm.getSentAt().toEpochMilli() : null
        );
    }
}