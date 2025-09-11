package com.airbng.core.service;

import com.airbng.core.domain.Member;
import com.airbng.core.domain.image.Image;
import com.airbng.core.exception.MemberException;
import com.airbng.core.repository.ImageRepository;
import com.airbng.core.repository.MemberRepository;
import com.airbng.platform.util.S3Utils;
import com.airbng.core.domain.base.BaseStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import static com.airbng.platform.common.response.status.BaseResponseStatus.NOT_FOUND_MEMBER;


@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ImageServiceImpl implements ImageService {
    private final ImageRepository imageRepository;
    private final S3Utils s3Utils;
    private final MemberRepository memberRepository;


    @Value("${image.default-url}")
    private String defaultImageUrl;

    @Override
    public Image uploadProfileImage(MultipartFile file) {
        s3Utils.createFileName(file.getOriginalFilename());

        Image image = Image.builder()
                .url(s3Utils.upload(file))
                .uploadName(file.getOriginalFilename())
                .status(BaseStatus.ACTIVE)
                .build();

        return imageRepository.save(image);
    }

    public Image getDefaultProfileImage() {
        Image image = Image.builder()
                .url(defaultImageUrl)
                .uploadName("default.jpg")
                .status(BaseStatus.ACTIVE)
                .build();
        imageRepository.save(image);
        return image;
    }

    @Transactional
    public Image updateProfileImage(MultipartFile file, Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(NOT_FOUND_MEMBER));

        Image image = member.getProfileImage();

        if (!image.getUploadName().equals("default.jpg")) {
            s3Utils.delete(image.getUrl());
        }

        String newUrl = s3Utils.upload(file);
        image.setUrl(newUrl);
        image.setUploadName(file.getOriginalFilename());

        return image;
    }
}
