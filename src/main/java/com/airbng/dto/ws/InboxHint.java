package com.airbng.dto.ws;

import lombok.*;
import java.time.Instant;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class InboxHint {
    private String convId;
    private String preview;     // last text
    private Instant sentAt;     // message time
    private Long senderId;      // who sent
    private Integer unreadTotal; // 상대방/나의 미확인 개수(모르면 null, 읽음이면 0)
}