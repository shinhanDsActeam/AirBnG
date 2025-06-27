package com.airbng.scheduler;

import com.airbng.domain.base.NotificationType;
import com.airbng.dto.NotificationRespose;
import com.airbng.dto.reservation.ReservationResponse;
import com.airbng.mappers.ReservationMapper;
import com.airbng.service.ReservationAlarmSseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class AlertScheduledTask {

    private final ReservationMapper reservationMapper;
    private final ReservationAlarmSseService sseService;

    @Scheduled(fixedRate = 1000 *60 * 5) // 5분마다 실행
    // 5분마다 실행
    public void processReservationAlarms() {
        LocalDateTime now = LocalDateTime.now();

        // 1. EXPIRED 알림 (24시간 지난 CONFIRMED)
        List<ReservationResponse> expired = reservationMapper.findExpiredConfirmedReservations(now.minusHours(24));
        for (ReservationResponse r : expired) {
            sendToBoth(r, NotificationType.EXPIRED, "짐 보관이 아직 완료되지 않았어요.", "고객 짐 보관 상태가 아직 완료되지 않았습니다.");
        }

        // 2. REMINDER 알림 (30분 전)
        List<ReservationResponse> remind = reservationMapper.findConfirmedNearEndTime(now.plusMinutes(30));
        for (ReservationResponse r : remind) {
            sendToOne(r.getDropper().getMemberId(), r.getReservationId(), r.getDropper().getNickname(), "DROPPER", NotificationType.REMINDER, "곧 짐을 찾아가셔야 해요.");
        }

        // 3. STATE_CHANGE 알림
        List<ReservationResponse> confirmed = reservationMapper.findStateChangedToConfirmed(now.minusMinutes(5));
        for (ReservationResponse r : confirmed) {
            sendToOne(r.getDropper().getMemberId(), r.getReservationId(), r.getDropper().getNickname(),"DROPPER", NotificationType.STATE_CHANGE, "예약이 확정되었습니다.");
        }

        // 4. CANCEL_NOTICE 알림
        List<ReservationResponse> cancelled = reservationMapper.findStateChangedToCancelled(now.minusMinutes(5));
        for (ReservationResponse r : cancelled) {
            sendToOne(r.getDropper().getMemberId(), r.getReservationId(), r.getDropper().getNickname(),"DROPPER", NotificationType.CANCEL_NOTICE, "예약이 취소되었습니다.");
        }
    }

    private void sendToBoth(ReservationResponse r, NotificationType type, String dropperMsg, String keeperMsg) {
        LocalDateTime now = LocalDateTime.now();
        NotificationRespose d = NotificationRespose.builder()
                .reservationId(r.getReservationId())
                .receiverId(r.getDropper().getMemberId())
                .nickName(r.getDropper().getNickname())
                .role("DROPPER")
                .type(type)
                .message(dropperMsg)
                .sendTime(now.toString()).build();
        NotificationRespose k = NotificationRespose.builder()
                .reservationId(r.getReservationId())
                .receiverId(r.getKeeper().getMemberId())
                .nickName(r.getKeeper().getNickname())
                .role("KEEPER")
                .type(type)
                .message(keeperMsg)
                .sendTime(now.toString()).build();
        if (sseService.hasConnected(d.getReceiverId())) sseService.sendMessage(d.getReceiverId(), d);
        if (sseService.hasConnected(k.getReceiverId())) sseService.sendMessage(k.getReceiverId(), k);
    }

    private void sendToOne(Long id, Long resId, String name, String role, NotificationType type, String message) {
        if (!sseService.hasConnected(id)) return;
        NotificationRespose dto = NotificationRespose.builder()
                .reservationId(resId)
                .receiverId(id)
                .nickName(name)
                .role(role)
                .type(type)
                .message(message)
                .sendTime(String.valueOf(LocalDateTime.now()))
                .build();
        sseService.sendMessage(id, dto);
    }
}