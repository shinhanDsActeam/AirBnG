package com.airbng.consumer.service;

import com.airbng.consumer.domain.base.NotificationType;

public interface ReservationAlarmCacheService {
    boolean isSent(Long reservationId, Long receiverId, NotificationType type);
    void markSent(Long reservationId, Long receiverId, NotificationType type);

    void markAllAsRead(Long memberId);
    void markUnread (Long memberId);
    boolean hasUnreadAlarm(Long memberId);

}
