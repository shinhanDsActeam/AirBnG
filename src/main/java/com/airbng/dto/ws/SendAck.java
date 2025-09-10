package com.airbng.dto.ws;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;

@Data
@AllArgsConstructor
public class SendAck {
    private String msgId;
    private Long seq;
    private Instant sentAt;
}
