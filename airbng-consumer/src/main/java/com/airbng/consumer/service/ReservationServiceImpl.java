package com.airbng.consumer.service;

import com.airbng.api.consumer.event.ReservationCreatedEvent;
import com.airbng.api.pay.PayApi;
import com.airbng.api.pay.RefundApi;
import com.airbng.api.pay.dto.command.MakePaymentRequest;
import com.airbng.api.pay.dto.command.RefundMode;
import com.airbng.api.pay.dto.command.RefundRequestCommand;
import com.airbng.api.pay.dto.view.RefundCardPayload;
import com.airbng.common.base.BaseStatus;
import com.airbng.consumer.domain.Locker;
import com.airbng.consumer.domain.Member;
import com.airbng.consumer.domain.Reservation;
import com.airbng.consumer.domain.base.*;
import com.airbng.consumer.domain.jimtype.JimType;
import com.airbng.consumer.domain.jimtype.ReservationJimType;
import com.airbng.consumer.dto.reservation.*;
import com.airbng.consumer.repository.*;
import com.airbng.consumer.exception.JimTypeException;
import com.airbng.consumer.exception.LockerException;
import com.airbng.consumer.exception.MemberException;
import com.airbng.consumer.exception.ReservationException;
import com.airbng.consumer.scheduler.AlertScheduledTask;
import com.airbng.platform.common.response.status.BaseResponseStatus;
import com.airbng.platform.security.principal.AirbngPrincipal;
import com.github.benmanes.caffeine.cache.Cache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantLock;

import static com.airbng.common.BusinessIds.ADMIN_MEMBER_ID;
import static com.airbng.platform.common.response.status.BaseResponseStatus.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReservationServiceImpl implements ReservationService {

    private final AlertScheduledTask alertScheduledTask;

    private final ReservationRepository reservationRepository;
    private final ReservationCustomRepository reservationCustomRepository;
    private final ReservationJimTypeRepository reservationJimTypeRepository;
    private final MemberRepository memberRepository;
    private final LockerRepository lockerRepository;
    private final JimTypeRepository jimTypeRepository;

    private final PayApi payApi;
    private final RefundApi refundApi;

    private static final Long LIMIT = 10L; // 페이지당 최대 예약 개수
    private final ApplicationEventPublisher events;

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
//            throw new ReservationException(NOT_FOUND_RESERVATION);
        }

        // 페이징 처리
        boolean hasNextPage = reservations.size() > LIMIT;
        List<ReservationSearchResponse> content = reservations.stream()
                .limit(LIMIT)
                .toList();

        // 다음 커서 ID 설정
        if (hasNextPage && !content.isEmpty()) {
            nextCursorId = content.get(content.size() - 1).getReservationId();
        } else {
            nextCursorId = -1L;  // 더 이상 페이지가 없으면 -1로 설정
        }

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
    public ReservationCancelResponse cancelReservation(Long reservationId, Long memberId) {
        log.info("[cancelReservation] 요청 (reservationId: {}) <pending -> cancel> by Member (ID: {})", reservationId, memberId);

        /** 락 */
        ReentrantLock lock = reservationLocks.get(reservationId, key -> new ReentrantLock());
        try {
            /** 락 걸기 */
            lock.lock();

            /** 맴버 존재 유무 파악 */
            Member member = memberRepository.findById(memberId)
                    .orElseThrow(() -> new MemberException(NOT_FOUND_MEMBER));

            /** 요청 예약건의 존재여부 파악 */
            Reservation reservation = reservationRepository.findByReservationId(reservationId)
                    .orElseThrow(() -> new ReservationException(NOT_FOUND_RESERVATION));

            /** 예약건의 dropper가 맞는지 파악 */
            if (!reservation.getDropper().getMemberId().equals(member.getMemberId()))
                throw new ReservationException(NOT_DROPPER_OF_RESERVATION);

            if (reservation.getState() == ReservationState.CANCELLED) {
                // 멱등하게 처리
                return ReservationCancelResponse.of(reservation,
                        ChargeType.from(reservation.getStartTime()).discountAmount(), ReservationState.CANCELLED);
            }

            ChargeType chargeType = ChargeType.from(reservation.getStartTime());
            ReservationState newState = ReservationState.CANCELLED;
            /** 취소, 완료상태는 상태 변경 불가 */
            ReservationState.canUpdate(MemberRole.DROPPER, reservation.getState(), newState);
            /** 삭제 상태는 상태 변경 불가 */
            reservation.isAvailableUpdateState();
            /** 더티 체킹 */
            reservation.updateState(newState);

            reservation.isAvailableUpdateState();

            RefundMode mode = (reservation.getState() == ReservationState.PENDING)
                    ? RefundMode.AUTO_FULL : RefundMode.REVIEW_REQUIRED;

            var payload = refundApi.requestRefund(
                    new RefundRequestCommand(
                            idemKey,
                            r.getReservationId(),
                            r.getPaymentId(),     // ★ 결제와 연결
                            actorId,
                            mode,
                            reason
                    )
            );

            // 전액 환불(AUTO_FULL)이면 즉시 취소 전이
            if (mode == RefundMode.AUTO_FULL) {
                r.updateState(ReservationState.CANCELLED);
            }




            log.info("[cancelReservation] 완료 - Reservation (ID: {}) state changed to {} by Dropper (ID: {})", reservationId, newState, memberId);

            return ReservationCancelResponse.of(reservation,
                    chargeType.discountAmount(), ReservationState.CANCELLED);

        } finally {
            /** 무조건 락 해제 */
            lock.unlock();
        }
    }

    @Override
    @Transactional
    public ReservationConfirmResponse confirmReservation(Long reservationId, boolean approve, Long memberId) {
        log.info("[confirmReservation] 요청 (reservationId: {}) state changed to {} by Member (ID: {})", reservationId, approve, memberId);
        ReentrantLock lock = reservationLocks.get(reservationId, key -> new ReentrantLock());
        try {
            lock.lock();
            /** 맴버 존재 유무 파악 */
            Member member = memberRepository.findById(memberId)
                    .orElseThrow(() -> new MemberException(NOT_FOUND_MEMBER));
            //예약건의 존재 여부 파악
            Reservation reservation = reservationRepository.findByReservationId(reservationId)
                    .orElseThrow(() -> new ReservationException(NOT_FOUND_RESERVATION));

            // 예약건의 keeper인지 확인
            if (!reservation.getKeeper().getMemberId().equals(member.getMemberId()))
                throw new ReservationException(NOT_KEEPER_OF_RESERVATION);

            // 요구된 상태값 저장
            ReservationState newState = approve ? ReservationState.CONFIRMED : ReservationState.REJECTED;

            // 이미 확정되었는지 확인
            if (reservation.getState() == newState) {
                // 멱등하게 처리
                return ReservationConfirmResponse.of(reservation, newState);
            }

            /** 상태변경 가능 여부 확인 */
            ReservationState.canUpdate(MemberRole.KEEPER, reservation.getState(), newState);
            /** 삭제 상태는 상태 변경 불가 */
            reservation.isAvailableUpdateState();

            String notificationMessage = "예약이 " + (approve ? "확정" : "거절") + "되었습니다.";

            /** 더티 체킹 */
            reservation.updateState(newState);

            if (reservation.getDropper() != null) {
                NotificationType notificationType = (newState == ReservationState.CONFIRMED) ?
                        NotificationType.STATE_CHANGE : NotificationType.CANCEL_NOTICE;

                /** 예약 승인/거절 알림 발송 */
                alertScheduledTask.sendToOne(reservation.getDropper().getMemberId(),
                        reservationId, reservation.getDropper().getNickname(),
                        MemberRole.DROPPER.name(), notificationType, notificationMessage);
            }

            log.info("[confirmReservation] 완료 - Reservation (ID: {}) state changed to {} by Keeper (ID: {})", reservationId, newState, memberId);
            return ReservationConfirmResponse.of(reservation, newState);
        } finally {
            lock.unlock();
        }
    }

    @Override
    @Transactional
    public ReservationCompleteResponse completeReservation(Long reservationId, Long memberId) {
        log.info("[completeReservation] 요청 (reservationId: {}) by Member (ID: {})", reservationId, memberId);

        ReentrantLock lock = reservationLocks.get(reservationId, key -> new ReentrantLock());
        try {
            /** 락 걸기 */
            lock.lock();

            /** 맴버 존재 유무 파악 */
            Member member = memberRepository.findById(memberId)
                    .orElseThrow(() -> new MemberException(NOT_FOUND_MEMBER));

            /** 요청 예약건의 존재여부 파악 */
            Reservation reservation = reservationRepository.findByReservationId(reservationId)
                    .orElseThrow(() -> new ReservationException(NOT_FOUND_RESERVATION));

            /** 삭제 상태는 상태 변경 불가 */
            reservation.isAvailableUpdateState();

            // 예약건의 참여자인지 파악
            MemberRole role;
            if (reservation.getDropper().getMemberId().equals(member.getMemberId())) {
                role = MemberRole.DROPPER;
            } else if (reservation.getKeeper().getMemberId().equals(member.getMemberId())) {
                role = MemberRole.KEEPER;
            } else {
                throw new ReservationException(NOT_PARTICIPANTS_OF_RESERVATION);
            }

            // 이미 완료되었는지 확인
            if (reservation.getState() == ReservationState.COMPLETED ||
                    (role == MemberRole.DROPPER && reservation.getState() == ReservationState.COMPLETING_DROPPER_ONLY) ||
                    (role == MemberRole.KEEPER && reservation.getState() == ReservationState.COMPLETING_KEEPER_ONLY)) {
                // 멱등하게 처리
                return ReservationCompleteResponse.from(reservation);
            }

            /** 예약건 상태변경 */
            ReservationState newState;
            if (role == MemberRole.DROPPER) {
                newState = (reservation.getState() == ReservationState.COMPLETING_KEEPER_ONLY)
                        ? ReservationState.COMPLETED
                        : ReservationState.COMPLETING_DROPPER_ONLY;
                ReservationState.canUpdate(MemberRole.DROPPER, reservation.getState(), newState);
            } else {
                newState = (reservation.getState() == ReservationState.COMPLETING_DROPPER_ONLY)
                        ? ReservationState.COMPLETED
                        : ReservationState.COMPLETING_KEEPER_ONLY;
                ReservationState.canUpdate(MemberRole.KEEPER, reservation.getState(), newState);
            }
            /** 더티 체킹 */
            reservation.updateState(newState);

            /** 예약 완료 알림 발송 */
            if (reservation.getState() != ReservationState.COMPLETED) {
                alertScheduledTask.sendToBoth(ReservationResponse.from(reservation),
                        NotificationType.COMPLETION_NOTICE,
                        "상대방이 예약 완료 처리를 하였습니다. 예약을 완료해 주세요.",
                        "상대방이 예약 완료 처리를 하였습니다. 예약을 완료해 주세요."
                );
            } else {
                alertScheduledTask.sendToBoth(ReservationResponse.from(reservation),
                        NotificationType.COMPLETION_NOTICE,
                        "예약이 완료되었습니다. 이용해주셔서 감사합니다.",
                        "예약이 완료되었습니다. 이용해주셔서 감사합니다."
                );
            }

            log.info("[completeReservation] 요청 (reservationId: {}) state changed to {} by Member (ID: {})", reservationId, newState, memberId);

            return ReservationCompleteResponse.from(reservation);
        } finally {
            /** 무조건 락 해제 */
            lock.unlock();
        }
    }


    @Override
    public ReservationDetailResponse findReservationDetail(Long reservationId, Long memberId) {
        Reservation reservation = reservationRepository.findReservationDetailById(reservationId)
                .orElseThrow(() -> new ReservationException(NOT_FOUND_RESERVATION));

        return ReservationDetailResponse.from(reservation);
    }

    @Override
    public ReservationFormResponse getReservationForm(Long lockerId) {
        Locker locker = lockerRepository.findLockerById(lockerId)
                .orElseThrow(() -> new LockerException(NOT_FOUND_LOCKER));

        return ReservationFormResponse.from(locker);
    }

    // 예약 등록

    /**
     * 예약 등록
     * 결제 실패 시 - 예약 등록까지 롤백
     * 짐타입 등록이 실패 시 - 예약 등록까지 롤백
     * 예약 등록 실패 시 - 결제 롤백
     *
     * @param request
     * @return
     */
    @Override
    @Transactional
    public ReservationInsertResponse insertReservation(final String idemKey, final ReservationInsertRequest request, final AirbngPrincipal principal) {
        log.info("insertReservation({})", request);

        validateStartTimeAndEndTime(request.getStartTime(), request.getEndTime());

        Locker locker = lockerRepository.findLockerById(request.getLockerId())
                .orElseThrow(() -> new LockerException(NOT_FOUND_LOCKER));

        validateIsAvailable(locker);

        Member dropper = memberRepository.findById(principal.getId())
                .orElseThrow(() -> new MemberException(INVALID_MEMBER));
        Member keeper = memberRepository.findById(locker.getKeeper().getMemberId())
                .orElseThrow(() -> new MemberException(INVALID_MEMBER));

        validateMember(dropper.getMemberId(), keeper.getMemberId());


        // 결제 정보 생성
        Long paymentId = payApi.pay(
                MakePaymentRequest.builder()
                        .amount(request.getAmount())
                        .fee(request.getFee())
                        .method(request.getPaymentMethod())
                        .idemKeyRaw(idemKey)
                        .payeeId(keeper.getMemberId())
                        .payerId(dropper.getMemberId())
                        .lockerId(locker.getLockerId())
                        .build());

        Optional<Reservation> reservationOptional = reservationRepository.findByPaymentId(paymentId);
        if (reservationOptional.isPresent()) {
            return ReservationInsertResponse.from(reservationOptional.get().getReservationId());
        }

        Reservation reservation = request.toEntity(dropper, keeper, paymentId, locker);

        request.getJimTypeCounts()
                .forEach(jtc -> {
                    JimType jt = jimTypeRepository.findById(jtc.getJimTypeId())
                            .orElseThrow(() -> new JimTypeException(INVALID_JIMTYPE));
                    validateJimTypes(locker, jt);
                    ReservationJimType reservationJimType
                            = ReservationJimType.of(reservation, jt, jtc.count);
                    reservation.addReservationJimType(reservationJimType);
                });

        reservationRepository.save(reservation);

        // 트랜잭션 커밋 후 카드 푸시되게 이벤트 발행
        events.publishEvent(ReservationCreatedEvent.of(
                reservation.getReservationId(),
                reservation.getDropper().getMemberId(),
                reservation.getKeeper().getMemberId()
        ));

        return ReservationInsertResponse.from(reservation.getReservationId());
    }

    @Transactional
    public RefundCardPayload requestRefundFromChat(String idemKey, Long reservationId, Long actorId, String reason) {
        Reservation r = reservationRepository.findReservationDetailById(reservationId)
                .orElseThrow(() -> new ReservationException(NOT_FOUND_RESERVATION));

        if (!r.getDropper().getMemberId().equals(actorId))
            throw new ReservationException(NOT_DROPPER_OF_RESERVATION);

        r.isAvailableUpdateState();

        RefundMode mode = (r.getState() == ReservationState.PENDING)
                ? RefundMode.AUTO_FULL : RefundMode.REVIEW_REQUIRED;

        var payload = refundApi.requestRefund(
                new RefundRequestCommand(
                        idemKey,
                        r.getReservationId(),
                        r.getPaymentId(),     // ★ 결제와 연결
                        actorId,
                        mode,
                        reason
                )
        );

        // 전액 환불(AUTO_FULL)이면 즉시 취소 전이
        if (mode == RefundMode.AUTO_FULL) {
            r.updateState(ReservationState.CANCELLED);
        }
        return payload;
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
        // 관리자 계정이 포함된 경우 예외
        if (dropperId == ADMIN_MEMBER_ID || keeperId == ADMIN_MEMBER_ID) {
            log.warn("관리자 계정이 예약에 포함됨. dropperId: {}, keeperId: {}", dropperId, keeperId);
            throw new ReservationException(INVALID_MEMBER);
        }
    }


    void validateIsAvailable(Locker locker) {
        if (!locker.getIsAvailable().isAvailable()) {
            throw new LockerException(BaseResponseStatus.LOCKER_NOT_AVAILABLE);
        }
    }

}
