package com.airbng.consumer.service;

import com.airbng.consumer.domain.base.NotificationType;
import com.airbng.consumer.dto.AlarmPayloadResponse;

import java.util.List;

public interface ReservationAlarmCacheService {

    void markAllAsRead(Long memberId);
    void markUnread (Long memberId);
    boolean hasUnreadAlarm(Long memberId);
    boolean tryMarkSent(Long reservationId, Long receiverId, NotificationType type);
    List<AlarmPayloadResponse> getMissedAlarms(Long memberId, String lastEventId);
    String saveAlarm(Long memberId, Object payload);

}
