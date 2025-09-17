package com.airbng.api.consumer.event;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class LockerApprovedEvent {
    private final Long memberId;
    private final String lockerName;
    private final Long pendingLockerId; // 필요시
}

