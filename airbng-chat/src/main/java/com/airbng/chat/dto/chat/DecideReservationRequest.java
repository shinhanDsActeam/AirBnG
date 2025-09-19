package com.airbng.chat.dto.chat;

import jakarta.validation.constraints.NotNull;

public record DecideReservationRequest(
        @NotNull
        Boolean approve,
        String reason
) {}