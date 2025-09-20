package com.airbng.api.consumer.event;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class LockerRejectedEvent {
    private final Long memberId;
    private final String lockerName;
    private final String reason;
}
