package com.airbng.controller;

import com.airbng.domain.base.NotificationType;
import com.airbng.dto.AlarmRespose;
import com.airbng.scheduler.AlertScheduledTask;
import com.airbng.service.ReservationAlarmSseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<String> triggerAlarms() {
        log.info("🧪 수동으로 알림 스케줄러 실행");
        alertScheduledTask.processReservationAlarms();
        return ResponseEntity.ok("알림 스케줄러가 실행되었습니다.");
    }

    @PostMapping("/test-notification/{memberId}")
    public ResponseEntity<String> testNotification(@PathVariable Long memberId) {
        log.info("🧪 테스트 알림 전송: memberId={}", memberId);

        AlarmRespose testNotification = AlarmRespose.builder()
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


//
//package com.airbng.controller;
//
//import com.airbng.domain.base.NotificationType;
//import com.airbng.dto.NotificationRespose;
//import com.airbng.scheduler.AlertScheduledTask;
//import com.airbng.service.ReservationAlarmSseService;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.time.LocalDateTime;
//
//@RestController
//@RequestMapping("/test")
//@RequiredArgsConstructor
//@Slf4j
//public class SchedulerManualTriggerController {
//
//    private final AlertScheduledTask alertScheduledTask;
//    private final ReservationAlarmSseService sseService;
//
//    @PostMapping("/trigger-alarms")
//    public ResponseEntity<String> triggerAlarms() {
//        try {
//            log.info("🧪 수동으로 알림 스케줄러 실행 시작");
//            alertScheduledTask.processReservationAlarms();
//            log.info("🧪 수동으로 알림 스케줄러 실행 완료");
//            return ResponseEntity.ok("알림 스케줄러가 성공적으로 실행되었습니다.");
//        } catch (Exception e) {
//            log.error("🧪 알림 스케줄러 실행 중 오류 발생", e);
//            return ResponseEntity.status(500).body("알림 스케줄러 실행 중 오류가 발생했습니다: " + e.getMessage());
//        }
//    }
//
//    @PostMapping("/test-notification/{memberId}")
//    public ResponseEntity<String> testNotification(@PathVariable Long memberId) {
//        try {
//            log.info("🧪 테스트 알림 전송 시작: memberId={}", memberId);
//
//            NotificationRespose testNotification = NotificationRespose.builder()
//                    .reservationId(999L) // 테스트용 예약 ID
//                    .receiverId(memberId)
//                    .role("DROPPER")
//                    .type(NotificationType.STATE_CHANGE)
//                    .message("테스트 알림입니다!")
//                    .sendTime(LocalDateTime.now().toString())
//                    .build();
//
//            boolean hasSent = sseService.hasConnected(memberId);
//            if (hasSent) {
//                sseService.sendMessage(memberId, testNotification);
//                log.info("🧪 테스트 알림 전송 완료: memberId={}", memberId);
//                return ResponseEntity.ok("테스트 알림이 성공적으로 전송되었습니다.");
//            } else {
//                log.warn("🧪 SSE 연결이 없는 사용자: memberId={}", memberId);
//                return ResponseEntity.ok("해당 사용자는 SSE에 연결되어 있지 않습니다. 먼저 SSE 연결을 해주세요.");
//            }
//        } catch (Exception e) {
//            log.error("🧪 테스트 알림 전송 중 오류 발생: memberId={}", memberId, e);
//            return ResponseEntity.status(500).body("테스트 알림 전송 중 오류가 발생했습니다: " + e.getMessage());
//        }
//    }
//
//    // 연결된 사용자 확인용 엔드포인트 추가
//    @GetMapping("/connections")
//    public ResponseEntity<String> getConnections() {
//        try {
//            // 이 메서드를 ReservationAlarmSseService에 추가해야 함
//            return ResponseEntity.ok("연결 상태 확인 완료");
//        } catch (Exception e) {
//            log.error("연결 상태 확인 중 오류 발생", e);
//            return ResponseEntity.status(500).body("연결 상태 확인 중 오류가 발생했습니다: " + e.getMessage());
//        }
//    }
//}