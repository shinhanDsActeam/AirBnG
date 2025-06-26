package com.airbng.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ReservationAlarmSseServiceImpl implements ReservationAlarmSseService {

    private static final Long TIMEOUT = 60L * 1000 * 60; // 60분
    // 클라이언트와의 SSE 연결을 관리하기 위한 맵
    private final Map<Long, SseEmitter> emitterMap = new ConcurrentHashMap<>();

    // 클라이언트가 SSE 연결을 요청할 때 호출되는 메서드
    @Override
    public SseEmitter connect(Long memberId, String lastEventId) {
        SseEmitter emitter = new SseEmitter(TIMEOUT);
        emitterMap.put(memberId, emitter);

        emitter.onTimeout(() -> {
            log.info("SSE 연결 타임아웃: memberId={}", memberId);
            emitterMap.remove(memberId);
        });

        emitter.onCompletion(() -> {
            log.info("SSE 연결 종료: memberId={}", memberId);
            emitterMap.remove(memberId);
        });

        emitter.onError(e -> {
            log.warn("SSE 오류 발생: memberId={}, error={}", memberId, e.getMessage());
            emitterMap.remove(memberId);
        });

        try {
            emitter.send(SseEmitter.event().name("connect").data("SSE 연결 성공"));
        } catch (IOException e) {
            log.error("초기 연결 메시지 전송 실패", e);
        }

        return emitter;
    }

    // 클라이언트에게 메시지를 전송하는 메서드
    @Override
    public void sendMessage(Long memberId, Object data) {
        SseEmitter emitter = emitterMap.get(memberId);
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event().name("alarm").data(data));
            } catch (IOException e) {
                log.warn("SSE 메시지 전송 실패: memberId={}, error={}", memberId, e.getMessage());
                emitterMap.remove(memberId);
            }
        }
    }

    // 클라이언트가 연결되어 있는지 확인하는 메서드
    @Override
    public boolean hasConnected(Long memberId) {
        return emitterMap.containsKey(memberId);
    }
}
