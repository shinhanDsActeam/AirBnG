package com.airbng.service;

import com.airbng.common.exception.LockerException;
import com.airbng.common.exception.MemberException;
import com.airbng.common.exception.ReservationException;
import com.airbng.common.response.status.BaseResponseStatus;
import com.airbng.dto.reservation.ReservationInsertRequest;
import com.airbng.mappers.JimTypeMapper;
import com.airbng.mappers.LockerMapper;
import com.airbng.mappers.MemberMapper;
import com.airbng.mappers.ReservationMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {
    private final ReservationMapper reservationMapper;
    private final JimTypeMapper jimTypeMapper;
    private final MemberMapper memberMapper;
    private final LockerMapper lockerMapper;
    // 예약 등록
    @Override
    @Transactional // 짐타입 등록 실패한 경우 예약 등록까지 롤백
    public BaseResponseStatus insertReservation(ReservationInsertRequest request) {
        // 예약 엔티티 추가
        reservationMapper.insertReservation(request);
        Long reservationId = reservationMapper.selectLastInsertId();
        // 위에서 insert한 예약 엔티티에 맞게 짐 타입 등록
        int cnt = jimTypeMapper.insertReservationJimTypes(reservationId, request.getJimTypeCounts());
        // 요청한 짐 타입 개수와 실제 등록된 개수가 일치하지 않는 경우 예외

        return BaseResponseStatus.CREATED_RESERVATION;
    }

}
