package com.airbng.core.mappers;

import com.airbng.core.domain.image.Image;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ImageMapper {
    void insertImage(Image image);

    Image findDefaultImage();

    Image updateDefaultProfileImage(Image image);

    Image findImageIdByMemberId(Long memberId);
}
