package com.airbng.chat.domain.model;

import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class ReservationCard {
    private Long   reservationId;
    private Long   lockerId;
    private String lockerName;
    private String address;

    private LocalDateTime startTime;   // MySQL 값 그대로 저장(프론트 포맷팅)
    private LocalDateTime endTime;

    private String category;           // "백팩/가방" (없으면 null)
    private String pickupMemo;         // "직접 집 건네주기" (없으면 null)

    private String imgUrl;       // 보관소 대표 이미지 (없으면 기본 이미지)

    private String status;      // PENDING/APPROVED/REJECTED/...
    private Boolean canApprove; // 호스트가 지금 누를 수 있는지(백 판단 결과)
}
