package com.airbng.api.consumer.dto.view;

import com.airbng.api.consumer.dto.common.ReservationStatus;

import java.time.LocalDateTime;

public record ReservationCardPayload(
        Long reservationId,
        Long dropperId,
        String dropperName,
        Long keeperId,
        String keeperName,
        Long lockerId,
        String lockerName,
        String address,
        LocalDateTime startTime,
        LocalDateTime endTime,
        String category,
        String pickupMemo,
        String imgUrl,
        ReservationStatus status,      // PENDING | CONFIRMED | REJECTED | CANCELED | COMPLETED
        Boolean canApprove  // 채팅호스트가 지금 승인/거절 가능 여부(상태+권한 반영)
) {}
