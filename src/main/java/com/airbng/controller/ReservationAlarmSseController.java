package com.airbng.controller;

import com.airbng.security.domain.CustomUserDetails;
import com.airbng.security.util.JwtUtil;
import com.airbng.service.ReservationAlarmSseService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
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

    @GetMapping(    value = "/reservations/alarms",
            produces = MediaType.TEXT_EVENT_STREAM_VALUE + ";charset=UTF-8")
    public SseEmitter subscribe(
            @RequestHeader(value = "Last-Event-ID", required = false) String lastEventId,
            @CookieValue(value = "sse", required = false) String sseToken
    ) {

        Long memberId = jwtUtil.getUserId(sseToken);
        log.info("memberId: {}", memberId);

        return reservationAlarmSseService.connect(memberId, lastEventId);
    }
}

