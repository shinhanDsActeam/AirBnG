package com.airbng.scheduler;

import com.airbng.common.exception.ReservationException;
import com.airbng.domain.Reservation;
import com.airbng.domain.base.NotificationType;
import com.airbng.dto.AlarmResponse;
import com.airbng.dto.reservation.ReservationResponse;
import com.airbng.mappers.ReservationMapper;
import com.airbng.repository.ReservationRepository;
import com.airbng.service.ReservationAlarmCacheService;
import com.airbng.service.ReservationAlarmSseService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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

    private final ReservationRepository reservationRepository;
    private final ReservationAlarmSseService sseService;
    private final ReservationAlarmCacheService reservationAlarmCacheService;
    @Scheduled(initialDelay = 10000, fixedRate = 1000 *30) //30초마다 실행 (테스트용)
//    @Scheduled(initialDelay = 10000, fixedRate = 1000 * 60 * 60 * 24)
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
            sendToBoth(r, NotificationType.EXPIRED, "짐 보관이 아직 완료되지 않았어요.", "고객 짐 보관 상태가 아직 완료되지 않았습니다.");
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
            // SSE 연결이 있으면 바로 전송
            if (sseService.hasConnected(r.getDropper().getMemberId())) {
                sseService.sendMessage(r.getDropper().getMemberId(), d);
            } else {
                // 접속 없으면 Redis에 저장, 안읽음 표시
                reservationAlarmCacheService.saveAlarm(r.getDropper().getMemberId(), d);
                reservationAlarmCacheService.markUnread(r.getDropper().getMemberId());
            }

            // 발송 완료 표시
            reservationAlarmCacheService.markSent(r.getReservationId(), r.getDropper().getMemberId(), type);
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

            // SSE 연결이 있으면 바로 전송
            if (sseService.hasConnected(r.getKeeper().getMemberId())) {
                sseService.sendMessage(r.getKeeper().getMemberId(), k);
            } else {
                // 접속 없으면 Redis에 저장, 안읽음 표시
                reservationAlarmCacheService.saveAlarm(r.getKeeper().getMemberId(), k);
                reservationAlarmCacheService.markUnread(r.getKeeper().getMemberId());
            }

            // 발송 완료 표시
//            reservationAlarmCacheService.markSent(r.getReservationId(), r.getKeeper().getMemberId(), type);
            log.info("EXPIRED 발송 완료 (keeper)");

            }
    }



    public void sendToOne(Long id, Long resId, String name, String role, NotificationType type, String message) {

        ObjectMapper mapper = new ObjectMapper();

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

        String jsonpayload; // JSON 직렬화된 문자열

        try {
            jsonpayload = mapper.writeValueAsString(dto);
        } catch (JsonProcessingException e) {
            log.error("AlarmResponse JSON 직렬화 실패", e);
            jsonpayload = dto.toString(); // fallback
        }

        // SSE 연결이 있으면 바로 전송
        if (sseService.hasConnected(id)) {
            sseService.sendMessage(id, dto);
        } else {
            reservationAlarmCacheService.saveAlarm(id, jsonpayload);
            reservationAlarmCacheService.markUnread(id);
        }

//        reservationAlarmCacheService.markSent(resId, id, type);
        log.info("REMINDER 발송 완료 (memberId={})", id);

    }
}