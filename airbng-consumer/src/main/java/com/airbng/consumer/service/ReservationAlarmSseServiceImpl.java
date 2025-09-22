package com.airbng.consumer.service;

import com.airbng.consumer.dto.AlarmPayloadResponse;
import com.airbng.consumer.auth.CustomUserDetails;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;


@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ReservationAlarmSseServiceImpl implements ReservationAlarmSseService {

    private static final Long TIMEOUT = 60L * 1000 * 60; // 60분

    // 알림 읽은 여부
    private final ReservationAlarmCacheService reservationAlarmCacheService;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    // 클라이언트와의 SSE 연결을 관리하기 위한 맵 (사용자가 여러개로 접속해도 sse연결 가능하도록 허용)
    //CopyOnWriteArrayList : 멀티스레드에 적합
    private final Map<Long, CopyOnWriteArrayList<SseEmitter>> emitterMap = new ConcurrentHashMap<>();

    // 클라이언트가 SSE 연결을 요청할 때 호출되는 메서드
    @Override
    public SseEmitter connect(Long memberId, String lastEventId) {

        log.info("SSE 연결 요청: memberId={}, 현재 연결 수={}", memberId, emitterMap.size());

        SseEmitter emitter = new SseEmitter(TIMEOUT);
        emitterMap.computeIfAbsent(memberId, k -> new CopyOnWriteArrayList<>()).add(emitter);

        emitter.onTimeout(() -> {
            log.info("SSE 연결 타임아웃: memberId={}", memberId);
            removeEmitter(memberId, emitter);
        });

        emitter.onCompletion(() -> {
            log.info("SSE 연결 종료: memberId={}", memberId);
            removeEmitter(memberId, emitter);
        });

        emitter.onError(e -> {
            log.warn("SSE 오류 발생: memberId={}, error={}", memberId, e.getMessage());
            removeEmitter(memberId, emitter);
        });

        try {
            emitter.send(SseEmitter.event().id("0").name("connect").data("SSE SUCCESS - memberId: " + memberId));

            if (lastEventId != null) {
                List<AlarmPayloadResponse> missedAlarms = reservationAlarmCacheService.getMissedAlarms(memberId, lastEventId);
                for (AlarmPayloadResponse alarm : missedAlarms) {
                    // Redis 시퀀스 ID 그대로 사용
                    log.info("놓친 알림 재전송: memberId={}, eventId={}, data={}", memberId, alarm.getEventId().toString(), alarm.getData());
                    // 놓친 알림 재전송
                    emitter.send(SseEmitter.event()
                            .id(alarm.getEventId().toString())
                            .name("alarm")
                            .data(objectMapper.writeValueAsString(alarm.getData()), MediaType.APPLICATION_JSON));
                }
            }

        } catch (IOException e) {
            log.error("초기 연결 메시지 전송 실패", e);
        }

        log.info("연결 완료: memberId={}, 현재 연결 수={}", memberId, emitterMap.size());

        return emitter;
    }

    // 클라이언트에게 메시지를 전송하는 메서드
    @Override
    public void sendMessage(Long memberId, Object payload) {
        List<SseEmitter> emitters = emitterMap.get(memberId);

        try {
            // 알림을 Redis에 저장하고 안읽음 표시
            String eventId = reservationAlarmCacheService.saveAlarm(memberId, payload);
            reservationAlarmCacheService.markUnread(memberId);

            // 각 emitter에 메시지 전송 시도 (존재 시 바로 전송)
            if (emitters != null && !emitters.isEmpty()) {
                log.info("알림 전송 시도: memberId={}, data={}", memberId, payload);

                List<SseEmitter> deadEmitters = new ArrayList<>();
                for (SseEmitter emitter : emitters) {
                    try {
                        emitter.send(SseEmitter.event().id(eventId).name("alarm").data(payload, MediaType.APPLICATION_JSON));
                    } catch (IOException e) {
                        log.warn("SSE 메시지 전송 실패: memberId={}, error={}", memberId, e.getMessage());
                        deadEmitters.add(emitter);
                        emitter.completeWithError(e);
                        removeEmitter(memberId, emitter);
                    }
                }
                // 연결 끊긴 emitter 정리
                emitters.removeAll(deadEmitters);
                if (emitters.isEmpty()) {
                    emitterMap.remove(memberId);
                }

            } else {
                log.info("SSE 연결 없음: memberId={}", memberId);
            }
        }catch(Exception e){
            log.error("알림 저장/전송 실패: memberId={}, error={}", memberId, e.getMessage());
        }
    }

    // 알림 읽음 처리
    @Override
    public void markAllAsRead(Long memberId) {
        reservationAlarmCacheService.markAllAsRead(memberId);
    }

    // 안읽은 알림 여부
    @Override
    public boolean hasUnreadAlarm(Long memberId) {
        return reservationAlarmCacheService.hasUnreadAlarm(memberId);
    }

    //연결 종료된 emitter을 제거 (한 사용자에게 여러 emitter가 존재 -> 끊긴 emitter만 제거)
    private void removeEmitter(Long memberId, SseEmitter emitter) {
        List<SseEmitter> emitters = emitterMap.get(memberId);
        if (emitters != null) {
            emitters.remove(emitter);
            if (emitters.isEmpty()) {
                emitterMap.remove(memberId);
            }
        }
    }
}
