package com.airbng.mappers;


import com.airbng.dto.LockerDetailResponse;
import org.apache.ibatis.annotations.Mapper;
import com.airbng.domain.base.ReservationState;
import com.airbng.dto.LockerPreviewResult;

import java.util.List;
@Mapper
public interface LockerMapper {
    LockerDetailResponse findUserById(Long lockerId);
    List<String> findImageById(Long lockerId);
    List<LockerPreviewResult> findTop5Lockers(ReservationState state);
  
}
