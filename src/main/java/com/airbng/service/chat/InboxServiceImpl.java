package com.airbng.service.chat;

import com.airbng.domain.chat.Conversation;
import com.airbng.domain.chat.Inbox;
import com.airbng.domain.chat.model.LastMessage;
import com.airbng.repository.chat.ConversationRepository;
import com.airbng.repository.chat.InboxRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.*;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InboxServiceImpl implements InboxService {

    private final InboxRepository inboxRepo;
    private final ConversationRepository conversationRepo;
    private final MongoTemplate mongo;

    @Override
    public void onNewMessage(long userId, long peerId, String convId, LastMessage last, Instant at, long senderId) {
        String inboxId = userId + ":" + convId;

        Query q = Query.query(Criteria.where("_id").is(inboxId));
        Update u = new Update()
                .set("id", inboxId)
                .set("userId", userId)
                .set("convId", convId)
                .set("peerId", peerId)
                .set("lastMessage", last)
                .set("lastMessageAt", at)
                .setOnInsert("lastReadSeq", 0L)
                .setOnInsert("cachedUnread", 0L);

        if (userId != senderId) {
            u.inc("cachedUnread", 1L);
        }
        mongo.upsert(q, u, Inbox.class);
    }

    @Override
    public void markRead(long userId, String convId, long lastSeenSeq) {
        String inboxId = userId + ":" + convId;

        Query q = Query.query(Criteria.where("_id").is(inboxId));
        Inbox updated = mongo.findAndModify(
                q,
                new Update().max("lastReadSeq", lastSeenSeq),
                FindAndModifyOptions.options().returnNew(true).upsert(true),
                Inbox.class
        );

        long newLastRead = (updated != null && updated.getLastReadSeq() != null) ? updated.getLastReadSeq() : lastSeenSeq;
        long highestSeq = conversationRepo.findById(convId).map(Conversation::getHighestSeq).orElse(0L);
        long unread = Math.max(0L, highestSeq - newLastRead);

        mongo.updateFirst(q, new Update().set("cachedUnread", unread), Inbox.class);
    }

    @Override
    public List<Inbox> getInbox(long userId, Pageable pageable) {
        return inboxRepo.findByUserIdOrderByLastMessageAtDesc(userId, pageable);
    }
}
