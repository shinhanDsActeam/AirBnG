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
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.locks.ReentrantLock;
import com.github.benmanes.caffeine.cache.Cache;

import static com.airbng.common.response.status.BaseResponseStatus.NOT_FOUND_RESERVATION;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService{

    private final ReservationMapper reservationMapper;
    private final Cache<Long, ReentrantLock> reservationLocks;


    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public ReservationCancelResponse updateReservationState(Long reservationId) {

        /** 락 만듬 */
        ReentrantLock lock = reservationLocks.get(reservationId, key -> new ReentrantLock());
        try {
            /** 릭 검 */
            lock.lock();
            /** 요청 예약건의 존재여부 파악 */
            Reservation reservation = reservationMapper.findReservationById(reservationId);
            if (reservation == null) {
                throw new ReservationException(NOT_FOUND_RESERVATION);
            }
            ChargeType chargeType = ChargeType.from(reservation.getStartTime());
            ReservationState state = reservation.getState();
            /** 취소, 완료상태는 상태 변경 불가 */
            state.isAvailableUpdate(state);
            reservationMapper.updateReservationState(reservationId,
                    ReservationState.CANCELLED);

             return ReservationCancelResponse.from(reservation,
                     chargeType.discountAmount(),ReservationState.CANCELLED);

            } finally{
                lock.unlock();
            }
    }

}
