package com.airbng.service;

import com.airbng.domain.base.NotificationType;

public interface ReservationAlarmCacheService {
    boolean isSent(Long reservationId, Long receiverId, NotificationType type);
    void markSent(Long reservationId, Long receiverId, NotificationType type);
}
