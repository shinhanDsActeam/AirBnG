package com.airbng.api.consumer;


import com.airbng.api.consumer.dto.command.ReservationDecisionCommand;
import com.airbng.api.consumer.dto.view.ReservationCardPayload;
import com.airbng.api.consumer.dto.view.ReservationDecisionResult;

public interface ReservationApi {

    /** 채팅 카드 렌더용으로 예약 정보를 요약 제공 */
    ReservationCardPayload getCardPayload(Long reservationId);

    /** 호스트가 승인/거절 액션을 수행 */
    ReservationDecisionResult decide(ReservationDecisionCommand cmd);
}
