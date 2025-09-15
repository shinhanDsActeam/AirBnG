package com.airbng.consumer.controller;

import com.airbng.consumer.service.ReservationAlarmSseService;
import com.airbng.platform.security.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@RequestMapping("/alarms")
public class AlarmReadCheckController {

    private final ReservationAlarmSseService reservationAlarmSseService;
    private final JwtUtil jwtUtil;

    @GetMapping("/unread")
    public boolean hasUnread(@CookieValue(value = "sse", required = false) String sseToken) {

        Long memberId = jwtUtil.getUserId(sseToken);
        return memberId != null && reservationAlarmSseService.hasUnreadAlarm(memberId);
    }
}
