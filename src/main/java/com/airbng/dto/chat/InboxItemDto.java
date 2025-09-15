package com.airbng.dto.chat;

import com.airbng.domain.chat.model.LastMessage;
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

    private LastMessage lastMessage;
    private Instant lastMessageAt;
    private Long cachedUnread;

    public String getDisplayName() {
        if (peerNickname != null && !peerNickname.isBlank()) return peerNickname;
        if (peerName != null && !peerName.isBlank()) return peerName;
        return "상대";
    }
}