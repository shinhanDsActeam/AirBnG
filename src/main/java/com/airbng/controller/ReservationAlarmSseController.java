package com.airbng.controller;

import com.airbng.security.domain.CustomUserDetails;
import com.airbng.security.util.JwtUtil;
import com.airbng.service.ReservationAlarmSseService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class ReservationAlarmSseController {

    private final ReservationAlarmSseService reservationAlarmSseService;
    private final JwtUtil jwtUtil;

    @GetMapping("/reservations/alarms")
    public SseEmitter subscribe(
            @RequestHeader(value = "Last-Event-ID", required = false) String lastEventId,
            @CookieValue(value = "sse", required = false) String sseToken
    ) {

//        Long memberId = userDetails != null ? userDetails.getId() : null;
        Long memberId = jwtUtil.getUserId(sseToken);
        log.info("memberId: {}", memberId);
        // lastEventId를 로그 또는 서비스로 넘겨서 놓친 알림 재전송할 수 있음
        return reservationAlarmSseService.connect(memberId, lastEventId);
    }
}

