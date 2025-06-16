package com.airbng.mappers;

import com.airbng.domain.Locker;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface LockerMapper {

    void insertLocker(Locker locker);

}
