package com.airbng.chat.dto.chat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data @NoArgsConstructor @AllArgsConstructor
public class InboxItemDto {
    private String convId;
    private Long peerId;
    private String peerName;
    private String peerNickname;
    private String peerProfileUrl;

    private LastMessageDto lastMessage;
    private Long lastMessageAtMs;
    private Long cachedUnread;

    public String getDisplayName() {
        if (peerNickname != null && !peerNickname.isBlank()) return peerNickname;
        if (peerName != null && !peerName.isBlank()) return peerName;
        return "상대";
    }
}