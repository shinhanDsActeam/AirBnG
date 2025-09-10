package com.airbng.dto.ws;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;

@Data
@AllArgsConstructor
public class TypingEvent {
    private long userId;
    private boolean typing;
    private Instant at;
}
