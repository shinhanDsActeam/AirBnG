package com.airbng.mappers;

import com.airbng.dto.LockerSearchDTO;

import java.util.List;

public interface LockerMapper {

    List<LockerSearchDTO.Result> lockerSearch(LockerSearchDTO.Result ls);
    int lockerSearchCount(LockerSearchDTO.Result ls);
}
