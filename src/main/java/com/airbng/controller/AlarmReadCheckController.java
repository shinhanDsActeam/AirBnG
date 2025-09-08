package com.airbng.controller;

import com.airbng.security.domain.CustomUserDetails;
import com.airbng.security.util.JwtUtil;
import com.airbng.service.ReservationAlarmSseService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/alarms")
public class AlarmReadCheckController {

    private final ReservationAlarmSseService reservationAlarmSseService;
    private final JwtUtil jwtUtil;

    @GetMapping("/unread")
    public boolean hasUnread(@CookieValue(value = "sse", required = false) String sseToken) {

        Long memberId = jwtUtil.getUserId(sseToken);
        return memberId != null && reservationAlarmSseService.hasUnreadAlarm(memberId);
    }
}
