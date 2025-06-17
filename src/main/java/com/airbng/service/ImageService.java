package com.airbng.service;

import com.airbng.domain.Image;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;


public interface ImageService {
    Image uploadProfileImage(MultipartFile file);
    Image getDefaultProfileImage();
}
