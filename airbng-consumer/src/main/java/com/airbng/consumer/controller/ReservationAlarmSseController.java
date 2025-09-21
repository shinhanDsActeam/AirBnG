package com.airbng.consumer.controller;

import com.airbng.consumer.service.ReservationAlarmSseService;
import com.airbng.platform.security.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RequestMapping("/alarms")
@RestController
@RequiredArgsConstructor
@Slf4j
public class ReservationAlarmSseController {

    private final ReservationAlarmSseService reservationAlarmSseService;
    private final JwtUtil jwtUtil;

    @GetMapping(value = "/reservations/alarms", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(
            @RequestHeader(value = "Last-Event-ID", required = false) String lastEventId,
            @CookieValue(value = "sse", required = false) String sseToken
    ) {

        Long memberId = jwtUtil.getUserId(sseToken);
        log.info("memberId: {}", memberId);

        return reservationAlarmSseService.connect(memberId, lastEventId);
    }
}

