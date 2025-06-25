package com.airbng.mappers;

import com.airbng.domain.Locker;
import com.airbng.domain.base.Available;
import com.airbng.domain.base.ReservationState;
import com.airbng.domain.image.Image;
import com.airbng.dto.locker.LockerDetailResponse;
import com.airbng.dto.locker.LockerPreviewResult;
import com.airbng.dto.locker.LockerSearchRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface LockerMapper {
    List<LockerPreviewResult> findAllLockerBySearch(LockerPreviewResult lpr);

    String findOneImageById(Long lockerId);

    Long findLockerCount(LockerPreviewResult lsr);

    List<LockerPreviewResult> findAllLockerBySearch(LockerSearchRequest condition);

    Long findLockerCount(LockerSearchRequest condition);

    LockerDetailResponse findUserById(Long lockerId);

    List<String> findImageById(Long lockerId);

    void insertLocker(Locker locker);

    void insertImage(Image image);

    void insertLockerImages(@Param("lockerId") Long lockerId, @Param("imageIds") List<Long> imageIds);

    void insertLockerJimTypes(@Param("lockerId") Long lockerId, @Param("jimTypeIds") List<Long> jimTypeIds);

    List<LockerPreviewResult> findTop5Lockers(ReservationState state);

    int findLockerByMemberId(@Param("memberId") Long memberId);

    List<Long> findValidJimTypeIds(@Param("jimTypeIds") List<Long> jimTypeIds);

    int findMemberId(@Param("memberId") Long memberId);

    boolean isExistLocker(Long lockerId);

    boolean isLockerKeeper(@Param("lockerId") Long lockerId, @Param("keeperId") Long keeperId);

    void updateLockerIsAvailable(@Param("lockerId") Long lockerId, @Param("isAvailable") Available isAvailable);

    void toggleLockerIsAvailable(@Param("lockerId") Long lockerId);

    Available getIsAvailableById(@Param("lockerId") Long lockerId);
}
