package com.airbng.mappers;

import com.airbng.dto.ReservationSearchResponse;
import com.airbng.dto.reservation.ReservationInsertRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ReservationMapper {
    void insertReservation(ReservationInsertRequest reservation);

    List<ReservationSearchResponse> findAllReservationById(
                                             @Param("memberId") Long memberId,
                                             @Param("role") String role,
                                             @Param("state") String state,
                                             @Param("nextCursorId") Long nextCursorId,
                                             @Param("limit") Long limit
    );

    Long findReservationByMemberId(@Param("memberId") Long memberId, @Param("role") String role);
}
