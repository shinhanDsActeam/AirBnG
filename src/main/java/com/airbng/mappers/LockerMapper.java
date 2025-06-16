package com.airbng.mappers;

import com.airbng.dto.PopularLockerDTO;

import java.util.List;

public interface LockerMapper {
    List<PopularLockerDTO.Result> selectTop5Lockers();
}
