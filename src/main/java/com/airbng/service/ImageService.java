package com.airbng.service;

import com.airbng.domain.image.Image;
import org.springframework.web.multipart.MultipartFile;

public interface ImageService {
    Image uploadProfileImage(MultipartFile file);
    Image getDefaultProfileImage();
    Image updateDefaultProfileImage(MultipartFile file, Long memberId);
}
