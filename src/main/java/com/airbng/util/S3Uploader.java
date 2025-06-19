package com.airbng.util;

import com.airbng.common.exception.ImageException;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

import static com.airbng.common.response.status.BaseResponseStatus.INVALID_EXTENSIONS;

@Component
@RequiredArgsConstructor
public class S3Uploader {

    private final AmazonS3Client amazonS3Client;
    private static final List<String> ALLOWED_EXTENSIONS = List.of("jpeg", "jpg", "png");

    @Value("${aws.s3.bucket}")
    private String bucket;

    private void validateFileExtension(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !originalFilename.contains(".")) {
            throw new ImageException(INVALID_EXTENSIONS);
        }

        String extension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new ImageException(INVALID_EXTENSIONS);
        }
    }

    public String upload(MultipartFile file, String filePath) throws IOException {
        validateFileExtension(file); // 확장자 검사 추가

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        metadata.setContentType(file.getContentType());

        amazonS3Client.putObject(
                new PutObjectRequest(bucket, filePath, file.getInputStream(), metadata)
        );

        return amazonS3Client.getUrl(bucket, filePath).toString();
    }
}
