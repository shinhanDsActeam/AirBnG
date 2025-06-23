package com.airbng.service;

import com.airbng.common.exception.MemberException;
import com.airbng.common.exception.ReservationException;
import com.airbng.domain.base.ChargeType;
import com.airbng.domain.Reservation;
import com.airbng.domain.base.ReservationState;
import com.airbng.dto.ReservationCancelResponse;
import com.airbng.mappers.MemberMapper;
import com.airbng.mappers.ReservationMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.locks.ReentrantLock;
import com.github.benmanes.caffeine.cache.Cache;

import static com.airbng.common.response.status.BaseResponseStatus.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService{

    private final ReservationMapper reservationMapper;
    private final MemberMapper memberMapper;

    private final Cache<Long, ReentrantLock> reservationLocks;


    @Override
    @Transactional
    public ReservationCancelResponse updateReservationState(Long reservationId, Long memberId) {

        /** 락 만듬 */
        ReentrantLock lock = reservationLocks.get(reservationId, key -> new ReentrantLock());
        try {
            /** 릭 검 */
            lock.lock();

            /** 맴버 존재 유무 파악 */
            if(!memberMapper.findById(memberId)) throw new MemberException(MEMBER_NOT_FOUND);

            /** 요청 예약건의 존재여부 파악 */
            Reservation reservation = reservationMapper.findReservationWithDropperById(reservationId);
            if (reservation == null) throw new ReservationException(NOT_FOUND_RESERVATION);

            /** 예약건의 주인이 맞는지 파악 */
            if(!reservation.getDropper().getMemberId().equals(memberId)) throw new ReservationException(NOT_DROPPER_OF_RESERVATION);

            ChargeType chargeType = ChargeType.from(reservation.getStartTime());
            ReservationState state = reservation.getState();
            /** 취소, 완료상태는 상태 변경 불가 */
            state.isAvailableUpdate(state);
            reservationMapper.updateReservationState(reservationId,
                    ReservationState.CANCELLED);

             return ReservationCancelResponse.from(reservation,
                     chargeType.discountAmount(),ReservationState.CANCELLED);

            } finally{
                /** 무조건 락 해제 */
                lock.unlock();
            }
    }

}
