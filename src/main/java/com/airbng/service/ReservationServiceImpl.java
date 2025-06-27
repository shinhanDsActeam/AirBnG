package com.airbng.service;

import com.airbng.common.exception.JimTypeException;
import com.airbng.common.exception.LockerException;
import com.airbng.common.exception.MemberException;
import com.airbng.common.exception.ReservationException;
import com.airbng.common.response.status.BaseResponseStatus;
import com.airbng.domain.Reservation;
import com.airbng.domain.base.ChargeType;
import com.airbng.domain.base.ReservationState;
import com.airbng.dto.jimType.JimTypeCountResult;
import com.airbng.dto.reservation.ReservationCancelResponse;
import com.airbng.dto.reservation.ReservationInsertRequest;
import com.airbng.dto.reservation.ReservationPaging;
import com.airbng.dto.reservation.ReservationSearchResponse;
import com.airbng.mappers.JimTypeMapper;
import com.airbng.mappers.LockerMapper;
import com.airbng.mappers.MemberMapper;
import com.airbng.mappers.ReservationMapper;
import com.airbng.util.LocalDateTimeUtils;
import com.github.benmanes.caffeine.cache.Cache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

import static com.airbng.common.response.status.BaseResponseStatus.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {

    private final ReservationMapper reservationMapper;
    private final JimTypeMapper jimTypeMapper;
    private final MemberMapper memberMapper;
    private final LockerMapper lockerMapper;
    private static final Long LIMIT = 10L; // 페이지당 최대 예약 개수

    //예약 조회 + 페이징 처리
    @Override
    public ReservationPaging findAllReservationById(Long memberId, String role, ReservationState state, Long nextCursorId) {
        log.info("Finding reservation by memberId: {}, role: {}, state: {}, nextCursorId: {}, LIMIT:{} ",
                memberId, role, state, nextCursorId, LIMIT);

        // 초기 커서 ID 설정
        if(nextCursorId == null) {
            nextCursorId = -1L;
        }

        String stateStr = (state != null) ? state.toString() : null;

        List<ReservationSearchResponse> reservations = reservationMapper.findAllReservationById(
                memberId, role, stateStr, nextCursorId, LIMIT + 1 //다음 페이지 유무 확인
        );


        // 예외 처리: 예약이 없을 경우
        if (reservations == null || reservations.isEmpty()) {
            throw new ReservationException(NOT_FOUND_RESERVATION);
        }

        //hasNextPage 값 설정 : 다음 페이지 유무
        boolean hasNextPage = reservations.size() > LIMIT;
        List<ReservationSearchResponse> content = reservations.stream()
                .limit(LIMIT)
                .collect(Collectors.toList());

        // role을 응답 DTO에 표시
        for (ReservationSearchResponse dto : content) {
            dto.setRole(role.toUpperCase()); // KEEPER or DROPPER
        }

        // 다음 커서 ID 설정
        if (hasNextPage && !content.isEmpty()) {
            nextCursorId = content.get(content.size() - 1).getReservationId();
        } else {
            nextCursorId = -1L;  // 더 이상 페이지가 없으면 -1로 설정
        }

        ReservationPaging paging = ReservationPaging.builder()
                .reservations(content)
                .nextCursorId(nextCursorId)
                .hasNextPage(hasNextPage)
                .totalCount(reservationMapper.findReservationByMemberId(memberId, role))
                .build();
        return paging;
    }

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
            if(!memberMapper.findById(memberId)) throw new MemberException(NOT_FOUND_MEMBER);

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

             return ReservationCancelResponse.of(reservation,
                     chargeType.discountAmount(),ReservationState.CANCELLED);

            } finally{
                /** 무조건 락 해제 */
                lock.unlock();
            }
    }

    // 예약 등록
    @Override
    @Transactional // 짐타입 등록 실패한 경우 예약 등록까지 롤백
    public BaseResponseStatus insertReservation(final ReservationInsertRequest request) {
        log.info("insertReservation({})", request);

        validateStartTimeAndEndTime(request.getStartTime(), request.getEndTime());
        validateMember(request.getDropperId(), request.getKeeperId());
        validateLockerKeeper(request.getLockerId(), request.getKeeperId());
        validateJimTypes(request.getLockerId(), request.getJimTypeCounts());

        reservationMapper.insertReservation(request);
        int cnt = jimTypeMapper.insertReservationJimTypes(request.getId(), request.getJimTypeCounts());

        if (cnt != request.getJimTypeCounts().size()) {
            throw new ReservationException(INVALID_JIMTYPE_COUNT);
        }

        return CREATED_RESERVATION;
    }

    private static void validateStartTimeAndEndTime(final String startTime, final String endTime) {
        if (LocalDateTimeUtils.isStartTimeAfterEndTime(startTime, endTime)
                || LocalDateTimeUtils.isStartTimeEqualEndTime(startTime, endTime)) {
            throw new ReservationException(INVALID_RESERVATION_TIME_ORDER);
        }
    }

    private void validateJimTypes(final Long lockerId, final List<JimTypeCountResult> jimTypeCounts) {
        List<Long> jimTypeIds = jimTypeCounts.stream()
                .map(JimTypeCountResult::getJimTypeId)
                .collect(Collectors.toList());
        if (!jimTypeMapper.validateLockerJimTypes(lockerId, jimTypeIds, jimTypeIds.size())) {
            throw new JimTypeException(LOCKER_DOES_NOT_SUPPORT_JIMTYPE);
        }
    }

    private void validateLockerKeeper(final Long lockerId, final Long keeperId) {
        if (!lockerMapper.isExistLocker(lockerId)) {
            throw new LockerException(NOT_FOUND_LOCKER);
        }
        if (!lockerMapper.isLockerKeeper(lockerId, keeperId)) {
            throw new LockerException(LOCKER_KEEPER_MISMATCH);
        }
    }

    private void validateMember(final Long dropperId, final Long keeperId) {
        // dropper와 keeper가 동일한 경우 예외
        if (dropperId.equals(keeperId)) {
            throw new ReservationException(INVALID_RESERVATION_PARTICIPANTS);
        }
        // dropper와 keeper 존재 여부 확인
        if (!memberMapper.isExistMember(dropperId)) {
            throw new MemberException(NOT_FOUND_MEMBER);
        }
        if (!memberMapper.isExistMember(keeperId)) {
            throw new MemberException(NOT_FOUND_MEMBER);
        }
    }


}
