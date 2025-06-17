package com.airbng.mappers;

import com.airbng.dto.LockerSearchDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface LockerMapper {
    List<LockerSearchDTO.Result> lockerSearch(LockerSearchDTO.Result ls);
    int lockerSearchCount(LockerSearchDTO.Result ls);
}
