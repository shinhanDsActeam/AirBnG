package com.airbng.repository;

import com.airbng.domain.Locker;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface LockerRepository {

    void insertLocker(Locker locker);

}
