package com.airbng.chat.domain.model;

import lombok.*;
import java.time.Instant;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class RefundCard {
    private Long refundId;
    private Long reservationId;
    private Long paymentId;
    private Long dropperId;
    private Long keeperId;
    private Long lockerId;
    private Long amount;       // DB 저장은 Long (nullable 허용)
    private Long feeToKeeper;  // DB 저장은 Long (nullable 허용)
    private String refundType; // "FULL" | "PARTIAL"
    private String status;     // "PENDING" | "COMPLETED" | "FAILED"
    private Instant createdAt;
}