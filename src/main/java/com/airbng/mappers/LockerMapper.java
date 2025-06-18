package com.airbng.mappers;

import com.airbng.dto.LockerPreviewResult;
import com.airbng.dto.LockerSearchRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface LockerMapper {
    List<LockerPreviewResult> findAllLockerBySearch(LockerPreviewResult lpr);
    String findOneImageById(Long lockerId);
    Long findLockerCount(LockerPreviewResult lsr);

    // 1. Locker 검색
    List<LockerPreviewResult> findAllLockerBySearch(LockerSearchRequest condition);

    // 2. Count 조회
    Long findLockerCount(LockerSearchRequest condition);
}