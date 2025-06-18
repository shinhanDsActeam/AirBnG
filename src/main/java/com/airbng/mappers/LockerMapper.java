package com.airbng.mappers;

import com.airbng.dto.LockerDetailResponse;
import org.apache.ibatis.annotations.Mapper;
import com.airbng.domain.base.ReservationState;
import com.airbng.dto.LockerPreviewResult;
import com.airbng.domain.Locker;
import com.airbng.domain.image.Image;
import com.airbng.domain.base.ReservationState;
import com.airbng.dto.LockerPreviewResult;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface LockerMapper {

    LockerDetailResponse findUserById(Long lockerId);
    List<String> findImageById(Long lockerId);
  
    void insertLocker(Locker locker);

    void insertImage(Image image);

    void insertLockerImages(@Param("lockerId") Long lockerId, @Param("imageIds") List<Long> imageIds);

    void insertLockerJimTypes(@Param("lockerId") Long lockerId, @Param("jimTypeIds") List<Long> jimTypeIds);

    List<LockerPreviewResult> findTop5Lockers(ReservationState state);

    int findLockerByMemberId(@Param("memberId") Long memberId);

    List<Long> findValidJimTypeIds(@Param("jimTypeIds") List<Long> jimTypeIds);

}
