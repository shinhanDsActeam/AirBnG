package com.airbng.service;

import com.airbng.common.exception.ImageException;
import com.airbng.domain.image.Image;
import com.airbng.mappers.ImageMapper;
import com.airbng.util.S3Uploader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

import static com.airbng.common.response.status.BaseResponseStatus.UPLOAD_FAILED;

@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {
    private final ImageMapper imageMapper;
    private final S3Uploader s3Uploader;

    @Override
    public Image uploadProfileImage(MultipartFile file) {

        String uuid = UUID.randomUUID().toString();
        String fileName = uuid + "_" + file.getOriginalFilename();
        String path = "profiles/" + fileName;

        String url = null;
        try {
            url = s3Uploader.upload(file, path);
        } catch (IOException e) {
            throw new ImageException(UPLOAD_FAILED);
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
