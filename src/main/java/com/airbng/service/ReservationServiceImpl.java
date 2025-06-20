package com.airbng.service;

import com.airbng.common.exception.ReservationException;
import com.airbng.domain.base.ChargeType;
import com.airbng.domain.Reservation;
import com.airbng.domain.base.ReservationState;
import com.airbng.dto.ReservationCancelResponse;
import com.airbng.mappers.ReservationMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

import static com.airbng.common.response.status.BaseResponseStatus.NOT_FOUND_RESERVATION;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService{

    private final ReservationMapper reservationMapper;

    /** 예약 락 선언  */
    private final Map<Long, ReentrantLock> reservationLocks = new ConcurrentHashMap<>();

    /**
     * TODO
     * 여기서 문제는 이제..
     * 승인과 동시에 취소일때가 문제인데..그런데 말입니다..  (o)
     * 예?? 생각이 안나요 어케하더라........................ . .. . . .. .. ..  .. .....  (o)
     * 아 수수료 ... 정책이 있데  (o)
     *  -- 예외처리 할 것---
     * 1. 아 맞다 맞다!!!!! 완료상태와 취소 상태는 업데이트 안되게 막아야지!!!!!!!!!!!! <- 예외처리 (o)
     * 2.
     *
     * */
    @Override
    public ReservationCancelResponse updateReservationState(Long reservationId) {
        /** 락 만듬 */
        ReentrantLock lock = reservationLocks.computeIfAbsent(reservationId, k -> new ReentrantLock());
        /** 릭 검 */
        lock.lock();
        try {
            /** 요청 예약건의 존재여부 파악 */
            Reservation reservation = reservationMapper.findReservationById(reservationId);
            ChargeType chargeType = ChargeType.from(reservation.getStartTime());
            ReservationState state = reservation.getState();
            /** 취소, 완료상태는 상태 변경 불가 */
            state.isAvailableUpdate(state);
            reservationMapper.updateReservationState(reservationId, ReservationState.CANCELLED);

            return ReservationCancelResponse.from(reservation,
                    chargeType.discountAmount(),ReservationState.CANCELLED);
        }catch (NullPointerException e){
            throw new ReservationException(NOT_FOUND_RESERVATION);
        }finally {
            lock.unlock();
        }
    }
}
