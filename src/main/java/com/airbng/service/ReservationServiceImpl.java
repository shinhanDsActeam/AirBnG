package com.airbng.service;

import com.airbng.common.exception.JimTypeException;
import com.airbng.common.exception.LockerException;
import com.airbng.common.exception.MemberException;
import com.airbng.common.exception.ReservationException;
import com.airbng.common.response.status.BaseResponseStatus;
import com.airbng.domain.Locker;
import com.airbng.domain.Member;
import com.airbng.domain.base.*;
import com.airbng.domain.Reservation;
import com.airbng.domain.base.ReservationState;
import com.airbng.domain.jimtype.JimType;
import com.airbng.domain.jimtype.ReservationJimType;
import com.airbng.dto.reservation.*;
import com.airbng.mappers.ReservationMapper;
import com.airbng.repository.*;
import com.airbng.scheduler.AlertScheduledTask;
import com.github.benmanes.caffeine.cache.Cache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import static com.airbng.common.response.status.BaseResponseStatus.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReservationServiceImpl implements ReservationService {

    private final AlertScheduledTask alertScheduledTask;

    private final ReservationMapper reservationMapper;

    private final ReservationRepository reservationRepository;
    private final  ReservationCustomRepository reservationCustomRepository;
    private final ReservationJimTypeRepository reservationJimTypeRepository;
    private final MemberRepository memberRepository;
    private final LockerRepository lockerRepository;
    private final JimTypeRepository jimTypeRepository;
    private static final Long LIMIT = 10L; // 페이지당 최대 예약 개수

    //예약 조회 + 페이징 처리
    @Override
    public ReservationPaging findAllReservationById(Long memberId, MemberRole role, Object state, Long nextCursorId, String period) {
        log.info("Finding reservation by memberId: {}, role: {}, state: {}, nextCursorId: {}, LIMIT:{},  PERIOD: {}",
                memberId, role, state, nextCursorId, LIMIT, period);


        List<ReservationState> stateList = null;

        if (state == null) {
            stateList = null;
        } else if (state instanceof List<?>) {
            stateList = (List<ReservationState>) state;
        } else {
            // 단일값이면 리스트로 감싸기
            stateList = Collections.singletonList((ReservationState) state);
        }

        // isHistoryTab 여부 판단
        boolean isHistoryTab = stateList != null &&
                (stateList.contains(ReservationState.COMPLETED) || stateList.contains(ReservationState.CANCELLED));

        List<ReservationSearchResponse> reservations = reservationCustomRepository.findAllReservationByMemberIdWithCursor(
                memberId, role, stateList, nextCursorId, LIMIT + 1, period, isHistoryTab);

        // 예외 처리: 예약이 없을 경우
        if (reservations == null || reservations.isEmpty()) {
            // 만약 isHistoryTab이 true라면, 예약이 없더라도 빈 페이지를 반환
            if (isHistoryTab) {
                return ReservationPaging.builder()
                        .reservations(Collections.emptyList())
                        .nextCursorId(-1L) // 더 이상 페이지가 없음
                        .hasNextPage(false)
                        .period(period)
                        .totalCount(0L)
                        .build();
            }
            //예약 조회에서 예약이 없으면 예외 발생
            throw new ReservationException(NOT_FOUND_RESERVATION);
        }

        // 페이징 처리
        boolean hasNextPage = reservations.size() > LIMIT;
        List<ReservationSearchResponse> content = reservations.stream()
                .limit(LIMIT)
                .peek(dto -> dto.setRole(role))
                .toList();

        // 다음 커서 ID
        nextCursorId = (hasNextPage && !content.isEmpty())
                ? content.get(content.size() - 1).getReservationId()
                : -1L;


        return ReservationPaging.builder()
                .reservations(content)
                .nextCursorId(nextCursorId)
                .hasNextPage(hasNextPage)
                .period(period)
                .build();
    }

    private final Cache<Long, ReentrantLock> reservationLocks;


    @Override
    @Transactional
    public ReservationCancelResponse updateReservationState(Long reservationId, Long memberId) {

        /** 락 만듬 */
        ReentrantLock lock = reservationLocks.get(reservationId, key -> new ReentrantLock());
        try {
            /** 락 걸기 */
            lock.lock();

            /** 맴버 존재 유무 파악 */
            if (!memberRepository.existsByMemberId(memberId)) throw new MemberException(NOT_FOUND_MEMBER);

            /** 요청 예약건의 존재여부 파악 */
            Reservation reservation = reservationRepository.findByReservationId(reservationId)
                    .orElseThrow(() -> new ReservationException(NOT_FOUND_RESERVATION));

            /** 예약건의 주인이 맞는지 파악 */
            if (!reservation.getDropper().getMemberId().equals(memberId)) throw new ReservationException(NOT_DROPPER_OF_RESERVATION);

            ChargeType chargeType = ChargeType.from(reservation.getStartTime());
            ReservationState state = reservation.getState();
            /** 취소, 완료상태는 상태 변경 불가 */
            state.isAvailableUpdate(state);
            /** 삭제 상태는 상태 변경 불가 */
            reservation.isAvailableUpdateState();
            /** 더티 체킹으로 대체 */
            reservation.updateState(ReservationState.CANCELLED);

            /** 예약 거절 알림 발송 */
            alertScheduledTask.sendToOne(reservation.getDropper().getMemberId(),
                    reservationId, reservation.getDropper().getNickname(),
                    "DROPPER", NotificationType.CANCEL_NOTICE, "예약이 취소되었습니다.");

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
            if (!memberRepository.existsByMemberId(memberId)) throw new MemberException(NOT_FOUND_MEMBER);

            //예약건의 존재 여부 파악
            Reservation reservation = reservationRepository.findByReservationId(reservationId)
                    .orElseThrow(() -> new ReservationException(NOT_FOUND_RESERVATION));

            //짐을 맡아주는 사람인지 확인
            if (!reservation.getKeeper().getMemberId().equals(memberId))
                throw new ReservationException(NOT_KEEPER_OF_RESERVATION);

            //취소, 완료상태는 상태변경 불가
            reservation.getState().isAvailableUpdate(reservation.getState());
            /** 삭제 상태는 상태 변경 불가 */
            reservation.isAvailableUpdateState();
            //상태값 저장
            ReservationState newState;
            String notificationMessage;

            if ("yes".equalsIgnoreCase(approve)) {
                newState = ReservationState.CONFIRMED;

                notificationMessage = "예약이 확정되었습니다.";

            } else if ("no".equalsIgnoreCase(approve)) {
                newState = ReservationState.CANCELLED;

                notificationMessage = "예약이 거절되었습니다.";

            } else {
                throw new ReservationException(CANNOT_UPDATE_STATE);
            }
            /** 더티 체킹으로 대체 */
            reservation.updateState(newState);


            if (reservation.getDropper() != null) {
                NotificationType notificationType = newState == ReservationState.CONFIRMED ?
                        NotificationType.STATE_CHANGE : NotificationType.CANCEL_NOTICE;
                log.info("알림 발송: memberId={}, reservationId={}, nickname={}, role=DROPPER, type={}, message={}",
                        reservation.getDropper().getMemberId(), reservationId,
                        reservation.getDropper().getNickname(), notificationType, notificationMessage);
                ;

                alertScheduledTask.sendToOne(
                        reservation.getDropper().getMemberId(),
                        reservationId,
                        reservation.getDropper().getNickname(),
                        "DROPPER",
                        notificationType,
                        notificationMessage
                );
            }

            return ReservationConfirmResponse.of(reservation, newState);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public ReservationDetailResponse findReservationDetail(Long reservationId, Long memberId) {
        Reservation reservation = reservationRepository.findByReservationId(reservationId)
                .orElseThrow(() -> new ReservationException(NOT_FOUND_RESERVATION));

        return ReservationDetailResponse.from(reservation);
    }

    @Override
    public ReservationFormResponse getReservationForm(Long lockerId) {
        Locker locker = lockerRepository.findLockerById(lockerId)
                .orElseThrow(()->new LockerException(NOT_FOUND_LOCKER));

        return ReservationFormResponse.from(locker);
    }

    // 예약 등록
    @Override
    @Transactional // 짐타입 등록 실패한 경우 예약 등록까지 롤백
    public BaseResponseStatus insertReservation(final ReservationInsertRequest request) {
        log.info("insertReservation({})", request);

        validateStartTimeAndEndTime(request.getStartTime(), request.getEndTime());

        Locker locker = lockerRepository.findLockerById(request.getLockerId())
                .orElseThrow(() -> new LockerException(NOT_FOUND_LOCKER));

        validateIsAvailable(locker);

        Member dropper = memberRepository.findById(request.getDropperId())
                .orElseThrow(() -> new MemberException(INVALID_MEMBER));
        Member keeper = memberRepository.findById(locker.getKeeper().getMemberId())
                .orElseThrow(() -> new MemberException(INVALID_MEMBER));

        validateMember(dropper.getMemberId(), keeper.getMemberId());

        Reservation reservation = request.toEntity(dropper, keeper);

        request.getJimTypeCounts()
                .forEach(jtc -> {
                    JimType jt = jimTypeRepository.findById(jtc.getJimTypeId())
                            .orElseThrow(() -> new JimTypeException(INVALID_JIMTYPE));
                    validateJimTypes(locker, jt);
                    ReservationJimType reservationJimType
                            = ReservationJimType.of(reservation,jt, jtc.count);
                    reservation.addReservationJimType(reservationJimType);
                });

        reservationRepository.save(reservation);

        return CREATED_RESERVATION;
    }

    @Override
    @Transactional
    public void deleteReservationById(Long reservationId) {
        Reservation reservation = reservationRepository.findByReservationId(reservationId)
                .orElseThrow(() -> new ReservationException(NOT_FOUND_RESERVATION));

        if (reservation.getState().equals(ReservationState.PENDING) || reservation.getState().equals(ReservationState.CONFIRMED)) {
            throw new ReservationException(FAILED_DELETE_RESERVATION);
        }

        reservation.updateStatus(BaseStatus.DELETE);
        reservationJimTypeRepository.updateStatusByReservationId(reservationId, BaseStatus.DELETE);
    }

    private static void validateStartTimeAndEndTime(final LocalDateTime startTime, final LocalDateTime endTime) {
        if (endTime.isBefore(startTime)
                || startTime.isAfter(endTime)) {
            throw new ReservationException(INVALID_RESERVATION_TIME_ORDER);
        }
    }

    private void validateJimTypes(final Locker locker, final JimType jimType) {
        if (!locker.validateLockerJimtype(jimType)) {
            throw new JimTypeException(LOCKER_DOES_NOT_SUPPORT_JIMTYPE);
        }
    }

    private void validateMember(final Long dropperId, final Long keeperId) {
        // dropper와 keeper가 동일한 경우 예외
        if (dropperId.equals(keeperId)) {
            throw new ReservationException(INVALID_RESERVATION_PARTICIPANTS);
        }
    }


    void validateIsAvailable(Locker locker) {
        if (!locker.getIsAvailable().isAvailable()) {
            throw new LockerException(BaseResponseStatus.LOCKER_NOT_AVAILABLE);
        }
    }

}
