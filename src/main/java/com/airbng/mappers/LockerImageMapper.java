package com.airbng.mappers;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface LockerImageMapper {

    void insertLockerImages(@Param("lockerId") Long lockerId,
                            @Param("imageIds") List<Long> imageIds);

}
