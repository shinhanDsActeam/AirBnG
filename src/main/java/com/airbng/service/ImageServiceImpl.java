package com.airbng.service;

import com.airbng.common.exception.ImageException;
import com.airbng.common.response.status.BaseResponseStatus;
import com.airbng.domain.image.Image;
import com.airbng.mappers.ImageMapper;
import com.airbng.util.S3Util;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {
    private final ImageMapper imageMapper;
    private final S3Util s3Util;

    @Override
    public Image uploadProfileImage(MultipartFile file) {

        String uuid = UUID.randomUUID().toString();
        String fileName = uuid + "_" + file.getOriginalFilename();
        String path = "profiles/" + fileName;

        String url = null;
        try {
            url = s3Util.upload(file, path);
        } catch (IOException e) {
            throw new ImageException(BaseResponseStatus.UPLOAD_FAILED);
        }

        Image image = Image.builder()
                .url(url)
                .uploadName(file.getOriginalFilename())
                .build();

        imageMapper.insertImage(image);
        return image;
    }

    public Image getDefaultProfileImage() {
        return imageMapper.findDefaultImage();
    }
}
