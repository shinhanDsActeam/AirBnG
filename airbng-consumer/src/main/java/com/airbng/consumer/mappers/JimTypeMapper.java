package com.airbng.consumer.mappers;

import com.airbng.consumer.domain.jimtype.ReservationJimType;
import com.airbng.consumer.dto.jimType.JimTypeCountResult;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface JimTypeMapper {
    void insertReservationJimType(ReservationJimType reservationJimType);
    int insertReservationJimTypes(@Param("reservationId") Long reservationId, @Param("results") List<JimTypeCountResult> results);

    boolean validateLockerJimTypes(@Param("lockerId") Long lockerId,
                                   @Param("jimTypeIds") List<Long> jimTypeIds,
                                   @Param("size") int size);
}
