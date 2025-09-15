package com.airbng.service.chat;

import com.airbng.domain.Member;
import com.airbng.domain.chat.Conversation;
import com.airbng.domain.chat.model.LastMessage;
import com.airbng.dto.chat.PeerProfileDto;
import com.airbng.repository.MemberRepository;
import com.airbng.repository.chat.ConversationRepository;
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
    private final MemberRepository memberRepo;

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

        Member m = memberRepo.findById(peerId).orElse(null);

        if (m == null) {
            // 최소한 id 만 제공
            return new PeerProfileDto(peerId, null, null, null);
        }

        // 프로필 URL 안전 추출
        String profileUrl = null;
        if (m.getProfileImage() != null) {
            profileUrl = m.getProfileImage().getUrl();
        }
        // Member 엔티티에 profileUrl 필드가 따로 있다면 fallback 처리
        if ((profileUrl == null || profileUrl.isBlank()) && m.getProfileImage() != null) {
            profileUrl = m.getProfileImage().getUrl();
        }

        return new PeerProfileDto(
                m.getMemberId(),
                m.getName(),
                m.getNickname(),
                profileUrl
        );
    }

}
