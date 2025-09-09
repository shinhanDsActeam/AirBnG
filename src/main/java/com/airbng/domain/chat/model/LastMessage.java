package com.airbng.domain.chat.model;

import lombok.*;

import java.time.Instant;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LastMessage {
    private String messageId;
    private Long senderId;
    private String type;             // text/image/...
    private String preview;
    private Instant sentAt;
}
