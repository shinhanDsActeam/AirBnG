package com.airbng.mappers;

import com.airbng.dto.ReservationSearchResponse;
import com.airbng.dto.reservation.ReservationInsertRequest;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface ReservationMapper {

    void insertReservation(ReservationInsertRequest reservation);

    List<ReservationSearchResponse> findAllReservationById(Map<String, Object> params);

}
