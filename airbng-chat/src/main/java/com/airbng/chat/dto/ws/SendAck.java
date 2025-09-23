package com.airbng.chat.dto.ws;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;

@Data
@AllArgsConstructor
public class SendAck {
    private String msgId;
    private Long seq;
    private Long sentAtMs;
}
