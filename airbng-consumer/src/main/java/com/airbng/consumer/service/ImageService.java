package com.airbng.consumer.service;

import com.airbng.consumer.domain.image.Image;
import org.springframework.web.multipart.MultipartFile;


public interface ImageService {
    Image uploadProfileImage(MultipartFile file);
    Image getDefaultProfileImage();
    Image updateProfileImage(MultipartFile file, Long memberId);
}
