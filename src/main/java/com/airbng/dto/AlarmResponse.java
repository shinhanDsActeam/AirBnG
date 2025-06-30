package com.airbng.dto;

import com.airbng.domain.base.NotificationType;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlarmResponse {
    private Long reservationId;

    private Long receiverId;         // 실제 알림 받는 유저 ID
    private String nickName;   // 실제 알림 받는 유저 이름

    private String role;     // "KEEPER" 또는 "DROPPER"

    private NotificationType type;             // "EXPIRED", "STATE_CHANGE" 등
    private String message;          // 사용자에게 보여줄 메시지
    private String sendTime;         // 발송 시각
}
