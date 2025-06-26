package com.airbng.service;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface ReservationAlarmSseService {

    /**
     * 클라이언트가 SSE 연결을 요청할 때 호출됩니다.
     * 클라이언트는 이 메서드를 통해 연결을 설정하고, 서버는 SseEmitter 객체를 반환합니다.
     * 클라이언트는 이 객체를 통해 서버로부터 이벤트를 수신할 수 있습니다.
     * @param memberId 클라이언트의 멤버 ID
     * @param lastEventId 마지막 이벤트 ID (옵션)
     *  클라이언트는 lastEventId를 통해 이전에 놓친 이벤트를 재전송받을 수 있습니다.
     * @return SseEmitter 객체
     */

    SseEmitter connect(Long memberId, String lastEventId);
    void sendMessage(Long memberId, Object data);
    boolean hasConnected(Long memberId);

}
