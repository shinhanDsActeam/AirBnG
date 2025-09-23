package com.airbng.chat.dto.chat;

import java.time.LocalDateTime;

public record ReservationCardDto(
        Long reservationId,
        Long lockerId,
        String lockerName,
        String address,
        LocalDateTime startTime,
        LocalDateTime endTime,
        String category,
        String pickupMemo,
        String imgUrl,
        String status,
        Boolean canApprove
) {}