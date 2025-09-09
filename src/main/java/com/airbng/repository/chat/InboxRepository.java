package com.airbng.repository.chat;

import com.airbng.domain.chat.Inbox;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface InboxRepository extends MongoRepository<Inbox, String> {
    Optional<Inbox> findByUserIdAndConvId(Long userId, String convId);
    List<Inbox> findByUserIdOrderByLastMessageAtDesc(Long userId, Pageable pageable);
}