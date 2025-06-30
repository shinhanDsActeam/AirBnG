package com.airbng.scheduler;

import com.airbng.common.exception.MemberException;
import com.airbng.common.exception.ReservationException;
import com.airbng.domain.base.NotificationType;
import com.airbng.dto.AlarmRespose;
import com.airbng.dto.reservation.ReservationResponse;
import com.airbng.mappers.ReservationMapper;
import com.airbng.service.ReservationAlarmCacheService;
import com.airbng.service.ReservationAlarmSseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

import static com.airbng.common.response.status.BaseResponseStatus.*;

@Slf4j
@Async
@Component
@RequiredArgsConstructor
public class AlertScheduledTask {

    private final ReservationMapper reservationMapper;
    private final ReservationAlarmSseService sseService;
    private final ReservationAlarmCacheService reservationAlarmCacheService;

    @Scheduled(initialDelay = 10000, fixedRate = 1000 * 60) //대기시간 10초, 1분 주기로 스케줄러 실행
    public void processReservationAlarms() {

        log.info("스케줄러 실행 - 현재 시간: {}", LocalDateTime.now());

        LocalDateTime now = LocalDateTime.now();

        // 1. EXPIRED 알림 (24시간 지난 CONFIRMED)
        List<ReservationResponse> expired = reservationMapper.findExpiredConfirmedReservations(now.minusHours(24));
        //예외처리
        if (expired == null) {
            throw new ReservationException(NOT_FOUND_EXPIRED_RESERVATION);
        }
        for (ReservationResponse r : expired) {
            sendToBoth(r, NotificationType.EXPIRED, "짐 보관이 아직 완료되지 않았어요.", "고객 짐 보관 상태가 아직 완료되지 않았습니다.");
        }

        // 2. REMINDER 알림 (30분 전)
        List<ReservationResponse> remind = reservationMapper.findConfirmedNearEndTime(now);
        //예외처리
        if (remind == null) {
            throw new ReservationException(NOT_FOUND_REMINDER_RESERVATION);
        }
        for (ReservationResponse r : remind) {
            sendToOne(r.getDropper().getMemberId(), r.getReservationId(), r.getDropper().getNickname(), "DROPPER", NotificationType.REMINDER, "곧 짐을 찾아가셔야 해요.");
        }

        // 3. STATE_CHANGE 알림
        List<ReservationResponse> confirmed = reservationMapper.findStateChangedToConfirmed();
        //예외처리
        if (confirmed == null) {
            throw new ReservationException(NOT_FOUND_STATE_CHANGE_RESERVATION);
        }
        for (ReservationResponse r : confirmed) {
            sendToOne(r.getDropper().getMemberId(), r.getReservationId(), r.getDropper().getNickname(),"DROPPER", NotificationType.STATE_CHANGE, "예약이 확정되었습니다.");
        }

        // 4. CANCEL_NOTICE 알림
        List<ReservationResponse> cancelled = reservationMapper.findStateChangedToCancelled();
        //예외처리
        if (cancelled == null) {
            throw new ReservationException(NOT_FOUND_CANCEL_NOTICE_RESERVATION);
        }
        for (ReservationResponse r : cancelled) {
            sendToOne(r.getDropper().getMemberId(), r.getReservationId(), r.getDropper().getNickname(),"DROPPER", NotificationType.CANCEL_NOTICE, "예약이 취소되었습니다.");
        }
    }

    private void sendToBoth(ReservationResponse r, NotificationType type, String dropperMsg, String keeperMsg) {
        LocalDateTime now = LocalDateTime.now();
        // DROPPER
        //레디스 캐시에 해당 내용의 알림 없으면 알림 발송
        if (!reservationAlarmCacheService.isSent(r.getReservationId(), r.getDropper().getMemberId(), type)) {
            if (sseService.hasConnected(r.getDropper().getMemberId())) {
                AlarmRespose d = AlarmRespose.builder()
                        .reservationId(r.getReservationId())
                        .receiverId(r.getDropper().getMemberId())
                        .nickName(r.getDropper().getNickname())
                        .role("DROPPER")
                        .type(type)
                        .message(dropperMsg)
                        .sendTime(now.toString()).build();
                sseService.sendMessage(r.getDropper().getMemberId(), d);

                //알림 보내고 레디스 캐시에 해당 내용 저장
                reservationAlarmCacheService.markSent(r.getReservationId(), r.getDropper().getMemberId(), type);
                log.info("✅ 발송 후 Redis markSent 완료 (dropper)");
            }
        } else {
            log.debug("🚫 DROPPER Redis에 발송됨 표시가 있어 재발송 안함");
        }

        // KEEPER
        //레디스 캐시에 해당 내용의 알림 없으면 알림 발송
        if (!reservationAlarmCacheService.isSent(r.getReservationId(), r.getKeeper().getMemberId(), type)) {
            if (sseService.hasConnected(r.getKeeper().getMemberId())) {
                AlarmRespose k = AlarmRespose.builder()
                        .reservationId(r.getReservationId())
                        .receiverId(r.getKeeper().getMemberId())
                        .nickName(r.getKeeper().getNickname())
                        .role("KEEPER")
                        .type(type)
                        .message(keeperMsg)
                        .sendTime(now.toString()).build();
                sseService.sendMessage(r.getKeeper().getMemberId(), k);

                //알림 보내고 레디스 캐시에 해당 내용 저장
                reservationAlarmCacheService.markSent(r.getReservationId(), r.getKeeper().getMemberId(), type);
                log.info("✅ 발송 후 Redis markSent 완료 (keeper)");
            }
        } else {
            log.debug("🚫 KEEPER Redis에 발송됨 표시가 있어 재발송 안함");
        }
    }

    private void sendToOne(Long id, Long resId, String name, String role, NotificationType type, String message) {
        if (!sseService.hasConnected(id)) return;
        if (reservationAlarmCacheService.isSent(resId, id, type)) {
            log.debug("🚫 이미 Redis에 발송됨 표시가 있어 재발송 안함 (reservationId={}, memberId={}, type={})", resId, id, type);
            return;
        }
        AlarmRespose dto = AlarmRespose.builder()
                .reservationId(resId)
                .receiverId(id)
                .nickName(name)
                .role(role)
                .type(type)
                .message(message)
                .sendTime(String.valueOf(LocalDateTime.now()))
                .build();
        sseService.sendMessage(id, dto);
        reservationAlarmCacheService.markSent(resId, id, type);
    }
}