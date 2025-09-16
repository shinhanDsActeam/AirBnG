package com.airbng.chat.repository;

import com.airbng.chat.domain.Inbox;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface InboxRepository extends MongoRepository<Inbox, String> {
    Optional<Inbox> findByUserIdAndConvId(Long userId, String convId);
    List<Inbox> findByUserIdOrderByLastMessageAtDesc(Long userId, Pageable pageable);
}