package com.airbng.domain.base;

public enum NotificationType {
    EXPIRED,        //CONFIRMED인데 endTime 24시간 지남
    STATE_CHANGE,   //예약 상태가 바뀜 (PENDING -> CONFIRMED / CONFIRMED -> CANCELLED )
    REMINDER,       //시간 다가올 때 리마인드
    CANCEL_NOTICE   //	예약 취소 알림
}