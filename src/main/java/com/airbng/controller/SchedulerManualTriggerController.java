package com.airbng.controller;

import com.airbng.domain.base.NotificationType;
import com.airbng.dto.AlarmResponse;
import com.airbng.scheduler.AlertScheduledTask;
import com.airbng.service.ReservationAlarmSseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
@Slf4j
public class SchedulerManualTriggerController {

    private final AlertScheduledTask alertScheduledTask;
    private final ReservationAlarmSseService sseService;

    @PostMapping("/trigger-alarms")
    @PreAuthorize("hasAnyAuthority('USER')")
    public ResponseEntity<String> triggerAlarms() {
        log.info("🧪 수동으로 알림 스케줄러 실행");
        alertScheduledTask.processReservationAlarms();
        return ResponseEntity.ok("알림 스케줄러가 실행되었습니다.");
    }

    @PostMapping("/test-notification/{memberId}")
    @PreAuthorize("hasAnyAuthority('USER')")
    public ResponseEntity<String> testNotification(@PathVariable Long memberId) {
        log.info("🧪 테스트 알림 전송: memberId={}", memberId);

        AlarmResponse testNotification = AlarmResponse.builder()
                .reservationId(3L)
                .receiverId(memberId)
                .nickName("테스트 사용자")
                .role("DROPPER")
                .type(NotificationType.STATE_CHANGE)
                .message("테스트 알림입니다!")
                .sendTime(LocalDateTime.now().toString())
                .build();

        sseService.sendMessage(memberId, testNotification);
        return ResponseEntity.ok("테스트 알림이 전송되었습니다.");
    }
}

