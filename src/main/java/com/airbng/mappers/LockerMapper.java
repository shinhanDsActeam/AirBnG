package com.airbng.mappers;

import com.airbng.domain.Locker;
import com.airbng.domain.image.Image;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface LockerMapper {
    void insertLocker(Locker locker);

    void insertImage(Image image);

    void insertLockerImages(@Param("lockerId") Long lockerId, @Param("imageIds") List<Long> imageIds);

    void insertLockerJimTypes(@Param("lockerId") Long lockerId, @Param("jimTypeIds") List<Long> jimTypeIds);

}
