package com.airbng.chat.domain;

import com.airbng.chat.domain.model.LastMessage;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document("conversation")
@CompoundIndex(name="pair_idx", def="{ 'userA': 1, 'userB': 1 }")
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class Conversation {
    @Id
    private String id;           // "minUser:maxUser"

    @Indexed private Long userA;
    @Indexed private Long userB;

    private Instant createdAt;
    private Long highestSeq;

    private LastMessage lastMessage;

    public static String makeId(long u1, long u2) {
        long a = Math.min(u1, u2), b = Math.max(u1, u2);
        return a + ":" + b;
    }
}