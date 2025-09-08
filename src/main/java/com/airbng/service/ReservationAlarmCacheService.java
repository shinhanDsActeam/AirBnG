package com.airbng.service;

import com.airbng.domain.base.NotificationType;
import com.airbng.dto.AlarmPayloadResponse;

import java.util.List;

public interface ReservationAlarmCacheService {
//    boolean isSent(Long reservationId, Long receiverId, NotificationType type);
//    void markSent(Long reservationId, Long receiverId, NotificationType type);

    void markAllAsRead(Long memberId);
    void markUnread (Long memberId);
    boolean hasUnreadAlarm(Long memberId);
    boolean tryMarkSent(Long reservationId, Long receiverId, NotificationType type);
    List<AlarmPayloadResponse> getMissedAlarms(Long memberId, String lastEventId);
    String saveAlarm(Long memberId, Object payload);

}
