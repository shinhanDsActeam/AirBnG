package com.airbng.consumer.scheduler;

import com.airbng.consumer.domain.Reservation;
import com.airbng.consumer.domain.base.NotificationType;
import com.airbng.consumer.dto.AlarmResponse;
import com.airbng.consumer.dto.reservation.ReservationResponse;
import com.airbng.consumer.exception.ReservationException;
import com.airbng.consumer.service.ReservationAlarmCacheService;
import com.airbng.consumer.service.ReservationAlarmSseService;
import com.airbng.consumer.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

import static com.airbng.platform.common.response.status.BaseResponseStatus.NOT_FOUND_EXPIRED_RESERVATION;
import static com.airbng.platform.common.response.status.BaseResponseStatus.NOT_FOUND_REMINDER_RESERVATION;

@Slf4j
@Async
@Component
@RequiredArgsConstructor
public class AlertScheduledTask {

    private final ReservationRepository reservationRepository;
    private final ReservationAlarmSseService sseService;
    private final ReservationAlarmCacheService reservationAlarmCacheService;

    @Scheduled(initialDelay = 10000, fixedRate = 1000 * 60 * 60 * 24)

    public void processReservationAlarms() {

        log.info("스케줄러 실행 - 현재 시간: {}", LocalDateTime.now());

        LocalDateTime now = LocalDateTime.now();

        // 1. EXPIRED 알림 (24시간 지난 CONFIRMED)
        List<Reservation> reservationExpired = reservationRepository.findExpiredConfirmedReservations(now.minusHours(24),now.minusHours(1));
        List<ReservationResponse> expired;
        //예외처리
        if (reservationExpired == null) {
            throw new ReservationException(NOT_FOUND_EXPIRED_RESERVATION);
        }else{
            expired = reservationExpired.stream()
                    .map(ReservationResponse::from)
                    .toList();
        }

        for (ReservationResponse r : expired) {
            sendToBoth(r, NotificationType.EXPIRED, "짐 보관이 아직 완료되지 않았어요.", "고객 짐 보관 상태가 아직 완료되지 않았어요.");
        }

        // 2. REMINDER 알림 (30분 전)
        List<Reservation> reservationRemind = reservationRepository.findConfirmedNearEndTime(now, now.plusMinutes(30));
        List<ReservationResponse> remind;
        //예외처리
        if (reservationRemind == null) {
            throw new ReservationException(NOT_FOUND_REMINDER_RESERVATION);
        }else{
            remind = reservationRemind.stream()
                    .map(ReservationResponse::from)
                    .toList();
        }

        for (ReservationResponse r : remind) {
            sendToOne(r.getDropper().getMemberId(), r.getReservationId(), r.getDropper().getNickname(), "DROPPER", NotificationType.REMINDER, "곧 짐을 찾아가셔야 해요.");
        }
    }

    public void sendToBoth(ReservationResponse r, NotificationType type, String dropperMsg, String keeperMsg) {
        LocalDateTime now = LocalDateTime.now();
        // DROPPER
        //레디스 캐시에 해당 내용의 알림 없으면 알림 발송
        if (reservationAlarmCacheService.tryMarkSent(r.getReservationId(), r.getDropper().getMemberId(), type)) {
            AlarmResponse d = AlarmResponse.builder()
                    .reservationId(r.getReservationId())
                    .receiverId(r.getDropper().getMemberId())
                    .nickName(r.getDropper().getNickname())
                    .role("DROPPER")
                    .type(type)
                    .message(dropperMsg)
                    .sendTime(now.toString()).build();

            sseService.sendMessage(r.getDropper().getMemberId(), d);

            log.info("EXPIRED 발송 완료 (dropper)");
        }

        // KEEPER
        //레디스 캐시에 해당 내용의 알림 없으면 알림 발송
        if (reservationAlarmCacheService.tryMarkSent(r.getReservationId(), r.getKeeper().getMemberId(), type)) {
            AlarmResponse k = AlarmResponse.builder()
                    .reservationId(r.getReservationId())
                    .receiverId(r.getKeeper().getMemberId())
                    .nickName(r.getKeeper().getNickname())
                    .role("KEEPER")
                    .type(type)
                    .message(keeperMsg)
                    .sendTime(now.toString()).build();

            sseService.sendMessage(r.getKeeper().getMemberId(), k);
            log.info("EXPIRED 발송 완료 (keeper)");
        }
    }


    public void sendToOne(Long id, Long resId, String name, String role, NotificationType type, String message) {

        if (!reservationAlarmCacheService.tryMarkSent(resId, id, type)) {
            log.debug("이미 Redis에 발송됨 표시가 있어 재발송 안함 (reservationId={}, memberId={}, type={})", resId, id, type);
            return;
        }
        AlarmResponse dto = AlarmResponse.builder()
                .reservationId(resId)
                .receiverId(id)
                .nickName(name)
                .role(role)
                .type(type)
                .message(message)
                .sendTime(String.valueOf(LocalDateTime.now()))
                .build();

        sseService.sendMessage(id, dto);
        log.info("REMINDER 발송 완료 (memberId={})", id);
    }

    public void sendLockerApproved(Long memberId, String lockerName) {
        AlarmResponse dto = AlarmResponse.builder()
                .receiverId(memberId)
                .type(NotificationType.LOCKER_APPROVED)
                .message("보관소 [" + lockerName + "]가 승인되었습니다.")
                .sendTime(String.valueOf(LocalDateTime.now()))
                .build();

        sseService.sendMessage(memberId, dto);
        log.info("LOCKER_APPROVED 알림 발송 완료 (memberId={}, lockerName={})", memberId, lockerName);
    }

    public void sendLockerRejected(Long memberId, String lockerName, String reason) {
        AlarmResponse dto = AlarmResponse.builder()
                .receiverId(memberId)
                .type(NotificationType.LOCKER_REJECTED)
                .message("보관소 [" + lockerName + "]가 반려되었습니다. 사유: " + reason)
                .sendTime(String.valueOf(LocalDateTime.now()))
                .build();

        sseService.sendMessage(memberId, dto);
        log.info("LOCKER_REJECTED 알림 발송 완료 (memberId={}, lockerName={})", memberId, lockerName);
    }

}