package com.airbng.chat.repository;

import com.airbng.chat.domain.Attachment;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface AttachmentRepository extends MongoRepository<Attachment, String> {
    List<Attachment> findByMessageId(String messageId); // message.msgId 참조
}