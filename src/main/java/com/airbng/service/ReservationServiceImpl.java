package com.airbng.service;

import com.airbng.common.exception.JimTypeException;
import com.airbng.common.exception.LockerException;
import com.airbng.common.exception.MemberException;
import com.airbng.common.exception.ReservationException;
import com.airbng.common.response.status.BaseResponseStatus;
import com.airbng.dto.jimType.JimTypeCountResult;
import com.airbng.dto.reservation.ReservationInsertRequest;
import com.airbng.dto.reservation.ReservationPaging;
import com.airbng.dto.reservation.ReservationSearchResponse;
import com.airbng.mappers.JimTypeMapper;
import com.airbng.mappers.LockerMapper;
import com.airbng.mappers.MemberMapper;
import com.airbng.mappers.ReservationMapper;
import com.airbng.util.LocalDateTimeUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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

    //예약 조회 + 페이징 처리
    @Override
    public ReservationPaging findAllReservationById(Long memberId, String role, String state, Long nextCursorId, Long limit) {
        log.info("Finding reservation by memberId: {}, role: {}, state: {}, nextCursorId: {}, limit:{} ",
                memberId, role, state, nextCursorId, limit);

        List<ReservationSearchResponse> reservations = reservationMapper.findAllReservationById(
                memberId, role, state, nextCursorId, limit + 1 //다음 페이지 유무 확인
        );

        // 예외 처리: 예약이 없을 경우
        if (reservations == null || reservations.isEmpty()) {
            throw new ReservationException(NOT_FOUND_RESERVATION);
        }

        //hasNextPage 값 설정 : 다음 페이지 유무
        boolean hasNextPage = reservations.size() > limit;
        List<ReservationSearchResponse> content = reservations.stream()
                .limit(limit)
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

    // 예약 등록
    @Override
    @Transactional // 짐타입 등록 실패한 경우 예약 등록까지 롤백
    public BaseResponseStatus insertReservation(ReservationInsertRequest request) {
        log.info("insertReservation({})", request);

        // startTime과 endTime이 유효한지 확인
        String startTime = request.getStartTime();
        String endTime = request.getEndTime();
        if (request.getStartTime() == null || request.getEndTime() == null) {
            throw new ReservationException(INVALID_RESERVATION_TIME);
        }

        if (LocalDateTimeUtils.isStartTimeAfterEndTime(startTime, endTime)
                || LocalDateTimeUtils.isStartTimeEqualEndTime(startTime, endTime)) {
            throw new ReservationException(INVALID_RESERVATION_TIME_ORDER);
        }

        // dropper와 keeper 존재 여부 확인
        boolean dropperFlag = memberMapper.isExistMember(request.getDropperId());
        if (!dropperFlag) {
            throw new MemberException(NOT_FOUND_MEMBER);
        }

        boolean keeperFlag = memberMapper.isExistMember(request.getKeeperId());
        if (!keeperFlag) {
            throw new MemberException(NOT_FOUND_MEMBER);
        }

        // dropper와 keeper가 동일한 경우 예외
        if (request.getDropperId().equals(request.getKeeperId())) {
            throw new ReservationException(INVALID_RESERVATION_PARTICIPANTS);
        }

        // 락커 존재 여부 확인
        boolean lockerFlag = lockerMapper.isExistLocker(request.getLockerId());
        if (!lockerFlag) {
            throw new LockerException(NOT_FOUND_LOCKER);
        }

        // keeper가 락커의 소유자인지 확인
        boolean isLockerKeeper = lockerMapper.isLockerKeeper(request.getLockerId(), request.getKeeperId());
        if (!isLockerKeeper) {
            throw new LockerException(LOCKER_KEEPER_MISMATCH);
        }

        // 예약 엔티티 추가
        reservationMapper.insertReservation(request);
        Long reservationId = request.getId();
        if (reservationId == null || reservationId < 1) {
            throw new ReservationException(CANNOT_CREATE_RESERVATION);
        }

        // 해당 보관소가 관리하는 짐타입들인지 검사
        List<Long> jimTypeIds = request.getJimTypeCounts().stream()
                .map(JimTypeCountResult::getJimTypeId)
                .collect(Collectors.toList());
        boolean validateJimtype = jimTypeMapper.validateLockerJimTypes(request.getLockerId(), jimTypeIds, jimTypeIds.size());
        if (!validateJimtype) {
            throw new JimTypeException(LOCKER_DOES_NOT_SUPPORT_JIMTYPE);
        }


        // 위에서 insert한 예약 엔티티에 맞게 짐 타입 등록
        int cnt = jimTypeMapper.insertReservationJimTypes(reservationId, request.getJimTypeCounts());
        // 요청한 짐 타입 개수와 실제 등록된 개수가 일치하지 않는 경우 예외
        // Mapper 에서 메서드가 실패하면 0을 반환
        if (cnt != request.getJimTypeCounts().size()) {
            throw new ReservationException(INVALID_JIMTYPE_COUNT);
        }

        return CREATED_RESERVATION;
    }


}
