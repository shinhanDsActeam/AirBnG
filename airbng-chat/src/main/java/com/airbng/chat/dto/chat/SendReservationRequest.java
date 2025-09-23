package com.airbng.chat.dto.chat;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;

public record SendReservationRequest(
        @NotNull
        Long reservationId,
        @NotBlank
        String msgId
) {}