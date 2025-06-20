package com.airbng.mappers;

import com.airbng.dto.ReservationSearchResponse;

import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ReservationMapper {

    List<ReservationSearchResponse> findAllReservationById(Map<String, Object> params);

}
