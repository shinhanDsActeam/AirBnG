package com.airbng.mappers;

import com.airbng.domain.Image;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ImageMapper {
    void insertImage(Image image);

    Image selectDefaultImage();
}
