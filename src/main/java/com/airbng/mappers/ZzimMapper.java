package com.airbng.mappers;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ZzimMapper {

    int isExistZzim(@Param("memberId") Long memberId, @Param("lockerId") Long lockerId);

    void insertZzim(@Param("memberId") Long memberId, @Param("lockerId") Long lockerId);

    void deleteZzim(@Param("memberId") Long memberId, @Param("lockerId") Long lockerId);

}
