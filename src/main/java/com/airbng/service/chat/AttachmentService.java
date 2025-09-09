package com.airbng.service.chat;

import com.airbng.domain.chat.Attachment;

import java.util.List;

public interface AttachmentService {
    Attachment save(Attachment a);
    List<Attachment> findByMessageId(String messageId);
}
