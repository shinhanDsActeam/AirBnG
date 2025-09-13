package com.airbng.consumer.controller;

import com.airbng.platform.security.util.JwtUtil;
import com.airbng.consumer.service.ReservationAlarmSseService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@RequestMapping("/alarms")
public class AlarmReadController {

    private final ReservationAlarmSseService reservationAlarmSseService;
    private final JwtUtil jwtUtil;

    @GetMapping("/read")
    public boolean hasread(@CookieValue(value = "sse", required = false) String sseToken) {

        Long memberId = jwtUtil.getUserId(sseToken);

        if (memberId == null) {
            return false; // 인증 안 된 경우 -> 안읽음 여부 false로
        }

        reservationAlarmSseService.markAllAsRead(memberId);
        // 읽음 여부 false (안읽은 알림 없음)
        return false;
    }
}