package com.airbng.service;

import com.airbng.common.exception.JimTypeException;
import com.airbng.common.exception.LockerException;
import com.airbng.common.exception.MemberException;
import com.airbng.common.exception.ReservationException;
import com.airbng.common.response.status.BaseResponseStatus;
import com.airbng.domain.Reservation;
import com.airbng.domain.base.Available;
import com.airbng.domain.base.ChargeType;
import com.airbng.domain.base.ReservationState;
import com.airbng.dto.jimType.JimTypeCountResult;
import com.airbng.dto.jimType.LockerJimTypeResult;
import com.airbng.dto.reservation.*;
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
    public ReservationPaging findAllReservationById(Long memberId, String role, ReservationState state, Long nextCursorId, String period) {
        log.info("Finding reservation by memberId: {}, role: {}, state: {}, nextCursorId: {}, LIMIT:{},  PERIOD: {}",
                memberId, role, state, nextCursorId, LIMIT, period);

        // 초기 커서 ID 설정
        if (nextCursorId == null) {
            nextCursorId = -1L;
        }

        String stateStr = (state != null) ? state.toString() : null;

        List<ReservationSearchResponse> reservations = reservationMapper.findAllReservationById(
                memberId, role, stateStr, nextCursorId, LIMIT + 1, period //다음 페이지 유무 확인
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
                .period(period)
                .totalCount(reservationMapper.findReservationByMemberId(memberId, role))
                .build();
        return paging;
    }

    private final Cache<Long, ReentrantLock> reservationLocks;


    @Override
    @Transactional
    public ReservationCancelResponse updateReservationState(Long reservationId, Long memberId) {

        /** 락 만듦 */
        ReentrantLock lock = reservationLocks.get(reservationId, key -> new ReentrantLock());
        try {
            /** 락 걸기 */
            lock.lock();

            /** 맴버 존재 유무 파악 */
            if (!memberMapper.findById(memberId)) throw new MemberException(NOT_FOUND_MEMBER);

            /** 요청 예약건의 존재여부 파악 */
            Reservation reservation = reservationMapper.findReservationWithDropperById(reservationId);
            if (reservation == null) throw new ReservationException(NOT_FOUND_RESERVATION);

            /** 예약건의 주인이 맞는지 파악 */
            if (!reservation.getDropper().getMemberId().equals(memberId))
                throw new ReservationException(NOT_DROPPER_OF_RESERVATION);

            ChargeType chargeType = ChargeType.from(reservation.getStartTime());
            ReservationState state = reservation.getState();
            /** 취소, 완료상태는 상태 변경 불가 */
            state.isAvailableUpdate(state);
            reservationMapper.updateReservationState(reservationId,
                    ReservationState.CANCELLED);

            return ReservationCancelResponse.of(reservation,
                    chargeType.discountAmount(), ReservationState.CANCELLED);

        } finally {
            /** 무조건 락 해제 */
            lock.unlock();
        }
    }

    @Override
    @Transactional
    public ReservationConfirmResponse confirmReservationState(Long reservationId, String approve, Long memberId) {
        ReentrantLock lock = reservationLocks.get(reservationId, key -> new ReentrantLock());
        try {
            //락 걸어
            lock.lock();
            //멤버 존재 유무 파악
            if (!memberMapper.findById(memberId)) throw new MemberException(NOT_FOUND_MEMBER);

            //예약건의 존재 여부 파악
            Reservation reservation = reservationMapper.findReservationWithKeeperById(reservationId);
            if (reservation == null) throw new ReservationException(NOT_FOUND_RESERVATION);

            //짐을 맡아주는 사람인지 확인
            if (!reservation.getKeeper().getMemberId().equals(memberId))
                throw new ReservationException(NOT_KEEPER_OF_RESERVATION);

            //취소, 완료상태는 상태변경 불가
            reservation.getState().isAvailableUpdate(reservation.getState());

            //상태값 저장
            ReservationState newState;
            if ("yes".equalsIgnoreCase(approve)) {
                newState = ReservationState.CONFIRMED;
            } else if ("no".equalsIgnoreCase(approve)) {
                newState = ReservationState.CANCELLED;
            } else {
                throw new ReservationException(CANNOT_UPDATE_STATE);
            }
            reservationMapper.updateReservationState(reservationId, newState);

            return ReservationConfirmResponse.of(reservation, newState);
        } finally {
            lock.unlock();
        }
    }


    @Override
    public ReservationDetailResponse findReservationDetail(Long reservationId, Long memberId) {
        Reservation reservation = reservationMapper.findReservationDetailById(reservationId);
        if (reservation == null) throw new ReservationException(NOT_FOUND_RESERVATION); // 보관소 있나요
        if (!reservation.getDropper().getMemberId().equals(memberId))
            throw new ReservationException(NOT_DROPPER_OF_RESERVATION); // 있는 보관소가 내거 맞나요

        return ReservationDetailResponse.from(reservation);
    }

    @Override
    public ReservationFormResponse getReservationForm(Long lockerId) {
        validateIsAvailable(lockerId);
        ReservationFormResponse response = lockerMapper.getLockerInfoById(lockerId);
        if (response == null)
            throw new LockerException(NOT_FOUND_LOCKER);
        List<LockerJimTypeResult> jimTypes = lockerMapper.getLockerJimTypeById(lockerId);
        response.setLockerJimTypes(jimTypes);
        return response;
    }

    // 예약 등록
    @Override
    @Transactional // 짐타입 등록 실패한 경우 예약 등록까지 롤백
    public BaseResponseStatus insertReservation(final ReservationInsertRequest request) {
        log.info("insertReservation({})", request);

        validateStartTimeAndEndTime(request.getStartTime(), request.getEndTime());
        validateLocker(request.getLockerId());
        validateJimTypes(request.getLockerId(), request.getJimTypeCounts());

        Long keeperId = lockerMapper.getLockerKepperId(request.getLockerId());
        request.setKeeperId(keeperId);
        validateMember(request.getDropperId(), keeperId);

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

    private void validateLocker(final Long lockerId) {
        if (!lockerMapper.isExistLocker(lockerId)) {
            throw new LockerException(NOT_FOUND_LOCKER);
        }
    }

    private void validateMember(final Long dropperId, final Long keeperId) {
        // dropper와 keeper가 동일한 경우 예외
        if (dropperId.equals(keeperId)) {
            throw new ReservationException(INVALID_RESERVATION_PARTICIPANTS);
        }
    }


    void validateIsAvailable(Long lockerId) {
        Available isAvailable = lockerMapper.getIsAvailableById(lockerId);
        if (isAvailable == Available.NO)
            throw new LockerException(BaseResponseStatus.LOCKER_NOT_AVAILABLE);
    }

}
