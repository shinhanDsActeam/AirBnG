package com.airbng.consumer.mappers;

import com.airbng.consumer.domain.Locker;
import com.airbng.common.base.Available;
import com.airbng.consumer.domain.base.ReservationState;
import com.airbng.consumer.domain.image.Image;
import com.airbng.consumer.dto.jimType.LockerJimTypeResult;
import com.airbng.consumer.dto.jimType.LockerJimTypeUpdateResult;
import com.airbng.consumer.dto.locker.LockerDetailResponse;
import com.airbng.consumer.dto.locker.LockerPreviewResult;
import com.airbng.consumer.dto.locker.LockerSearchRequest;
import com.airbng.consumer.dto.locker.LockerUpdateResponse;
import com.airbng.consumer.dto.reservation.ReservationFormResponse;
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

    LockerDetailResponse findLockerDetailByMemberId(@Param("memberId") Long memberId);

    LockerDetailResponse findLockerById(Long lockerId);

    LockerUpdateResponse findUpdateLockerDetailById(@Param("memberId") Long memberId);

    LockerUpdateResponse findUpdateLockerById(Long lockerId);

    List<String> findImageById(Long lockerId);

    void insertLocker(Locker locker);

    void insertImage(Image image);

    void insertLockerImages(@Param("lockerId") Long lockerId, @Param("imageIds") List<Long> imageIds);

    void insertLockerJimTypes(@Param("lockerId") Long lockerId, @Param("jimTypeIds") List<Long> jimTypeIds);

    List<LockerPreviewResult> findTop5Lockers(ReservationState state);

    int findLockerByMemberId(@Param("memberId") Long memberId);

    List<Long> findValidJimTypeIds(@Param("jimTypeIds") List<Long> jimTypeIds);

    List<LockerJimTypeUpdateResult> findAllJimTypes();

    int findMemberId(@Param("memberId") Long memberId);

    boolean isExistLocker(Long lockerId);

    boolean isLockerKeeper(@Param("lockerId") Long lockerId, @Param("keeperId") Long keeperId);

    void updateLockerIsAvailable(@Param("lockerId") Long lockerId, @Param("isAvailable") Available isAvailable);

    void toggleLockerIsAvailable(@Param("lockerId") Long lockerId);

    Available getIsAvailableById(@Param("lockerId") Long lockerId);

    ReservationFormResponse getLockerInfoById(@Param("lockerId") Long lockerId);

    List<LockerJimTypeResult> getLockerJimTypeById(@Param("lockerId") Long lockerId);

    Long getLockerKeeperId(@Param("lockerId") Long lockerId);

    void updateLockerInfo(Locker locker);

    void deleteLockerJimTypes(Long lockerId);

    void deleteLockerImages(Long lockerId);

    List<Long> findJimTypeIdsByLocker(Long lockerId);

    void deleteLocker(Long lockerId);
}
