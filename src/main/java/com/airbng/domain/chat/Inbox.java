package com.airbng.domain.chat;

import com.airbng.domain.chat.model.LastMessage;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document("inbox")
@CompoundIndex(name="user_conv_uq", def="{ 'userId': 1, 'convId': 1 }", unique = true)
@CompoundIndex(name="user_order_idx", def="{ 'userId': 1, 'lastMessageAt': -1 }")
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class Inbox {
    @Id
    private String id;               // "userId:convId"

    @Indexed private Long userId;
    @Indexed private String convId;

    private Long peerId;           // string

    private String peerName;         // 실명
    private String peerNickname;     // 닉네임
    private String peerProfileUrl;   // 프로필 이미지 URL

    private LastMessage lastMessage; // nullable (미러)

    private Instant lastMessageAt;
    private Long lastReadSeq;
    private Long cachedUnread;
}
