package com.airbng.repository.chat;

import com.airbng.domain.chat.Attachment;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface AttachmentRepository extends MongoRepository<Attachment, String> {
    List<Attachment> findByMessageId(String messageId); // message.msgId 참조
}