package com.airbng.repository.chat;

import com.airbng.domain.chat.Conversation;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ConversationRepository extends MongoRepository<Conversation, String> {
}