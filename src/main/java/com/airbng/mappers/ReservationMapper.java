package com.airbng.mappers;

import com.airbng.dto.ReservationPaging;
import com.airbng.dto.ReservationSearchResponse;

import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ReservationMapper {

    //List<ReservationSearchResponse> findAllReservationById(Map<String, Object> params);

    List<ReservationSearchResponse> findAllReservationById(
                                             @Param("memberId") Long memberId,
                                             @Param("role") String role,
                                             @Param("state") String state,
                                             @Param("nextCursorId") Long nextCursorId,
                                             @Param("limit") Long limit
    );
    Long findReservationByMemberId(@Param("memberId") Long memberId, @Param("role") String role);
}
