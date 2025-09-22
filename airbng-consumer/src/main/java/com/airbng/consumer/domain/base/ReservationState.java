package com.airbng.consumer.domain.base;

import com.airbng.consumer.exception.ReservationException;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

import static com.airbng.platform.common.response.status.BaseResponseStatus.CANNOT_UPDATE_STATE;

public enum ReservationState {
    /** dropper(예약자) 만 변경 가능 */
    PENDING,   // 예약 요청(대기)
    CANCELLED, // 예약 취소

    /** keeper(보관자) 만 변경 가능 */
    CONFIRMED, // 예약 승인(==확정)
    REJECTED , // 예약 거절

    /** 완료처리 */
    FINISHED_WAIT, // 완료 대기 (이용 후 아직 완료 처리 안함)
    COMPLETING_DROPPER_ONLY, // dropper(예약자) 만 완료 처리
    COMPLETING_KEEPER_ONLY, // keeper(보관자) 만 완료 처리
    COMPLETED; // 둘 다 완료 처리


    /**
     * 예약 상태 전이 맵
     * key: 현재 상태, value: 변경 가능한 상태 집합
     */
    private static final EnumMap<ReservationState, EnumSet<ReservationState>> DROPPER_UPDATABLE = new EnumMap<>(ReservationState.class);
    private static final EnumMap<ReservationState, EnumSet<ReservationState>> KEEPER_UPDATABLE = new EnumMap<>(ReservationState.class);

    static {
        // DROPPER 전이
        DROPPER_UPDATABLE.put(PENDING, EnumSet.of(CANCELLED));
        DROPPER_UPDATABLE.put(CONFIRMED, EnumSet.of(FINISHED_WAIT, COMPLETING_DROPPER_ONLY));
        DROPPER_UPDATABLE.put(COMPLETING_KEEPER_ONLY, EnumSet.of(COMPLETED));

        // KEEPER 전이
        KEEPER_UPDATABLE.put(PENDING, EnumSet.of(CONFIRMED, REJECTED));
        KEEPER_UPDATABLE.put(CONFIRMED, EnumSet.of(FINISHED_WAIT, COMPLETING_KEEPER_ONLY));
        KEEPER_UPDATABLE.put(COMPLETING_DROPPER_ONLY, EnumSet.of(COMPLETED));
    }
    /**
     * 예약 상태 변경 가능 여부 체크
     * @param memberRole 변경 요청자 역할
     * @param currentState 현재 상태
     * @param newState 변경할 상태
     */
    public static void canUpdate(MemberRole memberRole, ReservationState currentState, ReservationState newState) {
        if (MemberRole.DROPPER == memberRole) {
            if (!DROPPER_UPDATABLE.containsKey(currentState)
                    || !DROPPER_UPDATABLE.get(currentState).contains(newState)) {
                throw new ReservationException(CANNOT_UPDATE_STATE);
            }
        } else if (MemberRole.KEEPER == memberRole) {
            if (!KEEPER_UPDATABLE.containsKey(currentState)
                    || !KEEPER_UPDATABLE.get(currentState).contains(newState)) {
                throw new ReservationException(CANNOT_UPDATE_STATE);
            }
        } else {
            throw new ReservationException(CANNOT_UPDATE_STATE);
        }
    }


}