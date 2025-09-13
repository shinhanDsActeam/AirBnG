package com.airbng.consumer.controller;

import com.airbng.consumer.service.ReservationAlarmSseService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
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
    @PreAuthorize("hasAnyAuthority('USER')")
    public SseEmitter subscribe(
            HttpSession session,
            @RequestHeader(value = "Last-Event-ID", required = false) String lastEventId
    ) {

        // 세션에서 로그인한 사용자 정보 가져오기
        Long memberId = (Long) session.getAttribute("memberId");
        if (memberId == null) {
            throw new IllegalStateException("로그인 정보가 없습니다.");
        }

        // lastEventId를 로그 또는 서비스로 넘겨서 놓친 알림 재전송할 수 있음
        return reservationAlarmSseService.connect(memberId, lastEventId);
    }
}

