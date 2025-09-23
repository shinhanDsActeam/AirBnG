package com.airbng.chat.service;

import com.airbng.api.consumer.MemberApi;
import com.airbng.api.consumer.dto.view.MemberCardView;
import com.airbng.chat.domain.Conversation;
import com.airbng.chat.domain.model.LastMessage;
import com.airbng.chat.dto.chat.PeerProfileDto;
import com.airbng.chat.repository.ConversationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class ConversationServiceImpl implements ConversationService {

    private final ConversationRepository conversationRepo;
    private final MongoTemplate mongo;
    // MemberApi로 고쳐야함
    private final MemberApi memberApi;

    @Override
    public String makeConvId(long userA, long userB) {
        long a = Math.min(userA, userB), b = Math.max(userA, userB);
        return a + ":" + b;
    }

    @Override
    public Conversation getOrCreate(long userA, long userB) {
        String convId = makeConvId(userA, userB);
        return conversationRepo.findById(convId).orElseGet(() -> {
            Conversation c = Conversation.builder()
                    .id(convId)
                    .userA(Math.min(userA, userB))
                    .userB(Math.max(userA, userB))
                    .createdAt(Instant.now())
                    .highestSeq(0L)
                    .lastMessage(null)
                    .build();
            return conversationRepo.save(c);
        });
    }

    @Override
    public Conversation findById(String convId) {
        return conversationRepo.findById(convId).orElse(null);
    }

    @Override
    public void assertMember(String convId, long userId) {
        Conversation c = findById(convId);
        if (c == null || !(c.getUserA() == userId || c.getUserB() == userId)) {
            throw new IllegalArgumentException("Not a member of conversation: " + convId);
        }
    }

    @Override
    public long peerIdOf(String convId, long userId) {
        String[] p = convId.split(":");
        long a = Long.parseLong(p[0]), b = Long.parseLong(p[1]);
        if (userId == a) return b;
        if (userId == b) return a;
        throw new IllegalArgumentException("User not in conv: " + convId);
    }

    @Override
    public void updateOnNewMessage(String convId, LastMessage last, long seq) {
        Query q = Query.query(Criteria.where("_id").is(convId)
                .orOperator(Criteria.where("highestSeq").lt(seq), Criteria.where("highestSeq").exists(false)));
        Update u = new Update()
                .set("highestSeq", seq)
                .set("lastMessage", last);
        mongo.updateFirst(q, u, Conversation.class);
    }

    @Override
    public PeerProfileDto getPeerProfile(String convId, long requesterId) {
        assertMember(convId, requesterId);
        long peerId = peerIdOf(convId, requesterId);

        MemberCardView card = memberApi.findCardById(peerId);

        if (card == null) {
            // 최소한 id 만 제공
            return new PeerProfileDto(peerId, null, null, null);
        }

        String profileUrl = card.getImageUrl();
        if (profileUrl != null && profileUrl.isBlank()) profileUrl = null;

        return new PeerProfileDto(
                card.getMemberId(),
                card.getName(),
                card.getNickname(),
                profileUrl
        );
    }

}
