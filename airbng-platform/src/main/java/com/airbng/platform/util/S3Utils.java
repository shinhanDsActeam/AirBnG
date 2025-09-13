package com.airbng.platform.util;

import com.airbng.platform.common.exception.S3Exception;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.util.List;
import java.util.UUID;

import static com.airbng.platform.common.response.status.BaseResponseStatus.INVALID_EXTENSIONS;
import static com.airbng.platform.common.response.status.BaseResponseStatus.UPLOAD_FAILED;


@Component
@RequiredArgsConstructor
@Slf4j
public class S3Utils {

    private final AmazonS3Client amazonS3Client;
    private static final List<String> ALLOWED_EXTENSIONS = List.of("jpeg", "jpg", "png");

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public String upload(MultipartFile file) {

        String fileName = createFileName(file.getOriginalFilename());
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        metadata.setContentType(file.getContentType());

        validateFileExtension(file); // 확장자 검사 추가


        try (InputStream inputStream = file.getInputStream()) {
            amazonS3Client.putObject (new PutObjectRequest(bucket, fileName, inputStream, metadata));
        } catch (IOException e) {
            throw new S3Exception(UPLOAD_FAILED);
        }

        return amazonS3Client.getUrl(bucket, fileName).toString();
    }

    private void validateFileExtension(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !originalFilename.contains(".")) {
            throw new S3Exception(INVALID_EXTENSIONS);
        }

        String extension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new S3Exception(INVALID_EXTENSIONS);
        }
    }

    public void delete(String imageUrl) {
        log.info("Deleting image from S3. URL: {}", imageUrl);
        String splitStr = ".com/";
        String fileName = imageUrl.substring(imageUrl.lastIndexOf(splitStr) + splitStr.length());
        String decodedFileName = URLDecoder.decode(fileName);

        amazonS3Client.deleteObject(new DeleteObjectRequest(bucket, decodedFileName));

        // 삭제가 됐다면
        if (fileName == null || fileName.isEmpty()) {
            log.warn("Image URL is null or empty, skipping deletion");
            return;
        } else {
            log.info("Attempting to delete S3 object with key: {}", fileName);
        }
    }

    public String createFileName(String fileName) {
        String uuid = UUID.randomUUID().toString();

        String newFileName = uuid + "_" + fileName;
        String path = "profiles/" + newFileName;

        return path;
    }
}
