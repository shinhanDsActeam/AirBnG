package com.airbng.controller;

import com.airbng.security.domain.CustomUserDetails;
import com.airbng.security.util.JwtUtil;
import com.airbng.service.ReservationAlarmSseService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequiredArgsConstructor
@Controller
@RequestMapping("/page")
public class AlarmPageController {

    private final ReservationAlarmSseService reservationAlarmSseService;
    private final JwtUtil jwtUtil;

    @GetMapping("/notification")
    public String notificationPage(@CookieValue(value = "sse", required = false) String sseToken) {

        Long memberId = jwtUtil.getUserId(sseToken);

        if (memberId != null) {
            reservationAlarmSseService.markAllAsRead(memberId);   //알림 읽음 처리
        }

        return "notification";
    }

}
