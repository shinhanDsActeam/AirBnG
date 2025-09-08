package com.airbng.controller;

import com.airbng.security.domain.CustomUserDetails;
import com.airbng.service.ReservationAlarmSseService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RequestMapping("/alarms")
@RestController
@RequiredArgsConstructor
public class ReservationAlarmSseController {

    private final ReservationAlarmSseService reservationAlarmSseService;

    @GetMapping("/reservations/alarms")
    @PreAuthorize("hasAnyAuthority('USER')") //테스트땜에 주석처리
    public SseEmitter subscribe(
            @RequestHeader(value = "Last-Event-ID", required = false) String lastEventId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {

        Long memberId = userDetails != null ? userDetails.getId() : null;

        // lastEventId를 로그 또는 서비스로 넘겨서 놓친 알림 재전송할 수 있음
        return reservationAlarmSseService.connect(memberId, lastEventId);
    }
}

