package com.airbng.mappers;

import com.airbng.dto.reservation.ReservationInsertRequest;
import com.airbng.dto.reservation.ReservationSearchResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ReservationMapper {
    // 예약 등록
    void insertReservation(ReservationInsertRequest reservation);

    // 예약 조회 + 페이징 처리
    List<ReservationSearchResponse> findAllReservationById(
                                             @Param("memberId") Long memberId,
                                             @Param("role") String role,
                                             @Param("state") String state,
                                             @Param("nextCursorId") Long nextCursorId,
                                             @Param("limit") Long limit
    );

    //예약  목록 개수
    Long findReservationByMemberId(@Param("memberId") Long memberId, @Param("role") String role);
}
