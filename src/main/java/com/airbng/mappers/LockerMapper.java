package com.airbng.mappers;

import com.airbng.domain.base.ReservationState;
import com.airbng.dto.LockerPreviewResult;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface LockerMapper {
    List<LockerPreviewResult> findTop5Lockers(ReservationState state);
}
