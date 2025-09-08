package com.airbng.controller;

import com.airbng.security.domain.CustomUserDetails;
import com.airbng.service.ReservationAlarmSseService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/alarms")
public class AlarmReadCheckController {

    private final ReservationAlarmSseService reservationAlarmSseService;

    @GetMapping("/unread")
    public boolean hasUnread(@AuthenticationPrincipal CustomUserDetails userDetails) {
        Long memberId = userDetails != null ? userDetails.getId() : null;
        return memberId != null && reservationAlarmSseService.hasUnreadAlarm(memberId);
    }
}
