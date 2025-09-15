package com.airbng.service.chat;

import com.airbng.domain.chat.Conversation;
import com.airbng.domain.chat.Inbox;
import com.airbng.domain.chat.model.LastMessage;
import com.airbng.repository.MemberRepository;
import com.airbng.repository.chat.ConversationRepository;
import com.airbng.repository.chat.InboxRepository;
import com.airbng.repository.chat.MemberCardView;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.*;
import org.springframework.stereotype.Service;
import org.springframework.data.mongodb.core.aggregation.Aggregation;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class InboxServiceImpl implements InboxService {

    private final InboxRepository inboxRepo;
    private final ConversationRepository conversationRepo;
    private final MongoTemplate mongo;
    // TODO: MemberRepository 사용
    private final MemberRepository memberRepository;

    @Override
    public void onNewMessage(long userId, long peerId, String convId, LastMessage last, Instant at, long senderId) {
        String inboxId = userId + ":" + convId;

        // 1) peer 카드 (최초 insert 시에만 쓰려고 가져옴)
        MemberCardView card = null;
        var cards = memberRepository.findCardsByIds(List.of(peerId));
        if (!cards.isEmpty()) card = cards.get(0);

        Query q = Query.query(Criteria.where("_id").is(inboxId));
        Update u = new Update()
                .set("id", inboxId)
                .set("userId", userId)
                .set("convId", convId)
                .set("peerId", peerId)
                .set("lastMessage", last)
                .set("lastMessageAt", at);

        // 2) 디노말라이즈 필드는 "onInsert" 로만 (최초 생성 시에만 채움)
        if (card != null) {
            u.setOnInsert("peerName", card.getName());
            // 필요하다면 Inbox 문서에 peerNickname/peerProfileUrl 필드를 추가해도 됨
            u.setOnInsert("peerNickname", card.getNickname());
            u.setOnInsert("peerProfileUrl", card.getImageUrl());
        }

        if (userId == senderId) {
            // 보낸 사람: 항상 0으로 유지 (읽음)
            u.set("cachedUnread", 0L);
        } else {
            // 상대방: 미읽음 +1
            u.inc("cachedUnread", 1L);
        }
        mongo.upsert(q, u, Inbox.class);
    }

    @Override
    public void markRead(long userId, String convId, long lastSeenSeq) {
        final String inboxId = userId + ":" + convId;

        // conv 조회는 한 번만
        Conversation conv = conversationRepo.findById(convId).orElse(null);

        Long peerId = null;
        if (conv != null) {
            peerId = Objects.equals(conv.getUserA(), userId) ? conv.getUserB() : conv.getUserA();
        }

        MemberCardView card = null;
        if (peerId != null) {
            var cards = memberRepository.findCardsByIds(List.of(peerId));
            if (!cards.isEmpty()) card = cards.get(0);
        }

        Query q = Query.query(Criteria.where("_id").is(inboxId));

        // lastReadSeq는 오직 $max로만! (setOnInsert로 같이 쓰지 않음)
        Update u = new Update()
                .max("lastReadSeq", lastSeenSeq)
                .setOnInsert("id", inboxId)
                .setOnInsert("userId", userId)
                .setOnInsert("convId", convId)
                .setOnInsert("peerId", peerId);

        if (card != null) {
            u.setOnInsert("peerName", card.getName());
            u.setOnInsert("peerNickname", card.getNickname());
            u.setOnInsert("peerProfileUrl", card.getImageUrl());
        }

        Inbox updated = mongo.findAndModify(
                q,
                u,
                FindAndModifyOptions.options().returnNew(true).upsert(true),
                Inbox.class
        );

        long newLastRead = (updated != null && updated.getLastReadSeq() != null)
                ? updated.getLastReadSeq()
                : lastSeenSeq;

        long highestSeq = (conv != null && conv.getHighestSeq() != null) ? conv.getHighestSeq() : 0L;
        long unread = Math.max(0L, highestSeq - newLastRead);

        mongo.updateFirst(q, new Update().set("cachedUnread", unread), Inbox.class);
    }

    @Override
    public List<Inbox> getInbox(long userId, Pageable pageable) {
        return inboxRepo.findByUserIdOrderByLastMessageAtDesc(userId, pageable);
    }

    @Override
    public Inbox getOne(long userId, String convId) {
        return inboxRepo.findByUserIdAndConvId(userId, convId).orElse(null);
    }

    @Override
    public int increaseUnreadAndGet(long userId, String convId) {
        final String inboxId = userId + ":" + convId;

        // 0) convId에서 peerId 계산 (convId는 "min:max" 형식)
        Long peerId = null;
        try {
            String[] parts = convId.split(":");
            long a = Long.parseLong(parts[0]);
            long b = Long.parseLong(parts[1]);
            if (userId == a) peerId = b;
            else if (userId == b) peerId = a;
            // else: 잘못된 convId(내가 멤버가 아님) → peerId 그대로 null
        } catch (Exception ignore) {
            // convId 포맷 문제 시 null 유지
        }

        // 1) 최초 생성 시에만 쓸 peer 카드(이름/닉네임/이미지) 준비
        MemberCardView card = null;
        if (peerId != null) {
            var cards = memberRepository.findCardsByIds(java.util.List.of(peerId));
            if (!cards.isEmpty()) card = cards.get(0);
        }

        // 2) upsert + inc (문서 없으면 생성, 있으면 unread +1)
        Query q = Query.query(Criteria.where("_id").is(inboxId));

        Update u = new Update()
                .setOnInsert("id", inboxId)
                .setOnInsert("userId", userId)
                .setOnInsert("convId", convId)
                .setOnInsert("cachedUnread", 0L)   // ← 생성 시 0에서 시작
                .setOnInsert("peerId", peerId);

        if (card != null) {
            u.setOnInsert("peerName", card.getName());
            u.setOnInsert("peerNickname", card.getNickname());
            u.setOnInsert("peerProfileUrl", card.getImageUrl());
        }

        // 실제 증가
        u.inc("cachedUnread", 1L);

        Inbox updated = mongo.findAndModify(
                q,
                u,
                FindAndModifyOptions.options()
                        .returnNew(true)
                        .upsert(true),
                Inbox.class
        );

        long unread = (updated != null && updated.getCachedUnread() != null)
                ? updated.getCachedUnread()
                : 0L;

        // 서비스 시그니처가 int라서 캐스팅 (너무 큰 값 보호하려면 Math.toIntExact 대신 min 사용)
        return (unread > Integer.MAX_VALUE) ? Integer.MAX_VALUE : (int) unread;
    }

    @Override
    public int totalUnread(long userId) {
        var agg = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("userId").is(userId)),
                Aggregation.group().sum("cachedUnread").as("total")
        );
        var doc = mongo.aggregate(agg, Inbox.class, Document.class).getUniqueMappedResult();

        // Mongo Document에서 Long/Integer 혼재 가능 → Number로 받고 intValue()
        Number n = (doc != null) ? (Number) doc.get("total") : 0;
        return (n != null) ? n.intValue() : 0;
    }
}
