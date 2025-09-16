package com.airbng.chat.dto.chat;

import com.airbng.chat.domain.Message;

public record MessageDto(
        String id,
        String convId,
        Long   seq,
        String msgId,
        Long   senderId,
        String senderName,
        String type,
        String text,
        Long   sentAtMs,     // ← epoch ms (Instant -> toEpochMilli)
        Boolean deleted
) {
    public static MessageDto from(Message m) {
        return new MessageDto(
                m.getId(),
                m.getConvId(),
                m.getSeq(),
                m.getMsgId(),
                m.getSenderId(),
                m.getSenderName(),
                m.getType(),
                m.getText(),
                m.getSentAt() != null ? m.getSentAt().toEpochMilli() : null,
                Boolean.TRUE.equals(m.getDeleted())
        );
    }
}