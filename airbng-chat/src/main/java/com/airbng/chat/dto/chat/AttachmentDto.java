package com.airbng.chat.dto.chat;

public record AttachmentDto(
        String attachmentId,
        String kind,
        String mime,
        Long   size,
        Integer width,
        Integer height,
        String fileName,
        String imageUrl
) {}