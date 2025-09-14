package com.airbng.admin.service;

import com.airbng.admin.api.PendingLockerInsertRequest;
import com.airbng.admin.common.exception.LockerException;
import com.airbng.admin.domain.PendingLockerImage;
import com.airbng.admin.domain.review.PendingLockerJimtype;
import com.airbng.admin.repository.LockerReviewImageRepository;
import com.airbng.admin.repository.LockerReviewJimTypeRepository;
import com.airbng.common.exception.ImageException;
import com.airbng.domain.base.BaseStatus;
import com.airbng.admin.domain.base.ReviewStatus;
import com.airbng.admin.domain.review.LockerReview;
import com.airbng.admin.domain.review.PendingLocker;
import com.airbng.admin.dto.LockerReviewConfirmResponse;
import com.airbng.admin.dto.LockerReviewDetailResponse;
import com.airbng.admin.dto.LockerReviewListResponse;
import com.airbng.admin.repository.LockerReviewRepository;
import com.airbng.domain.image.Image;
import com.airbng.domain.image.LockerImage;
import com.airbng.domain.jimtype.JimType;
import com.airbng.domain.jimtype.LockerJimType;
import com.github.benmanes.caffeine.cache.Cache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import static com.airbng.admin.common.response.status.BaseResponseStatus.*;
import static com.airbng.common.response.status.BaseResponseStatus.DUPLICATE_JIMTYPE;
import static com.airbng.common.response.status.BaseResponseStatus.EMPTY_FILE;
import static com.airbng.common.response.status.BaseResponseStatus.EXCEED_IMAGE_COUNT;
import static com.airbng.common.response.status.BaseResponseStatus.INVALID_JIMTYPE;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LockerReviewServiceImpl implements LockerReviewService{

    private final LockerReviewRepository lockerReviewRepository;
    private final LockerReviewImageRepository lockerReviewImageRepository;
    private final LockerReviewJimTypeRepository lockerReviewJimTypeRepository;

    //저장
    @Override
    public void insertPendingLocker(PendingLockerInsertRequest dto) {
        // PendingLocker 엔티티 생성
        PendingLocker pendingLocker = PendingLocker.builder()
                .lockerName(dto.getLockerName())
                .address(dto.getAddress())
                .addressEnglish(dto.getAddressEnglish())
                .addressDetail(dto.getAddressDetail())
                .latitude(dto.getLatitude())
                .longitude(dto.getLongitude())
                .memberId(dto.getMemberId())
                .reviewStatus(dto.getReviewStatus())
                .status(dto.getStatus())
                .build();

        lockerReviewRepository.save(pendingLocker);


        // 이미지 저장/연결
        if (dto.getImage() != null && !dto.getImage().isEmpty()) {
            if (dto.getImage().size() > 5) throw new ImageException(EXCEED_IMAGE_COUNT);

            for (MultipartFile file : dto.getImage()) {

                if (file.isEmpty()) {
                    throw new ImageException(EMPTY_FILE);
                }

                s3Utils.createFileName(file.getOriginalFilename());

                Image image = Image.builder()
                        .url(s3Utils.upload(file))
                        .uploadName(file.getOriginalFilename())
                        .status(BaseStatus.ACTIVE)
                        .build();
                imageRepository.save(image);

                lockerReviewImageRepository.save(
                        LockerImage.builder().pendingLocker(pendingLocker).image(image).status(BaseStatus.ACTIVE).build()
                );
            }
        }

        // 짐타입 연결
        List<Long> jimTypeIds = dto.getJimTypeId();
        if (jimTypeIds != null && !jimTypeIds.isEmpty()) {
            if (new HashSet<>(jimTypeIds).size() != jimTypeIds.size())
                throw new com.airbng.common.exception.LockerException(DUPLICATE_JIMTYPE);


            List<JimType> types = jimTypeRepository.findAllById(jimTypeIds);
            for (JimType t : types) {
                lockerReviewJimTypeRepository.save(
                        LockerJimType.builder().pendingLocker(pendingLocker).jimType(t).status(BaseStatus.ACTIVE).build()
                );
            }
        }
    }


    // ================= 상세 =================
    @Override
    public LockerReviewDetailResponse findLockerReviewById(Long lockerReviewId) {
        PendingLocker pendingLocker = lockerReviewRepository.findLockerReviewById(lockerReviewId)
                .orElseThrow(() -> new LockerException(NOT_FOUND_LOCKERDETAILS));

        LockerReview lr = pendingLocker.getReviewComment();

        return LockerReviewDetailResponse.from(pendingLocker);
    }


    // ================= 승인/거절 =================
//    @Override
//    public void approve(Long pendingLockerId){
//        PendingLocker pendingLocker = lockerReviewRepository.findById(pendingLockerId)
//                .orElseThrow(() -> new LockerException(NOT_FOUND_LOCKER));
//
//        // 승인 로직: PendingLocker → Locker 엔티티 변환 후 저장
//        // (이미지, jimType 복사 등)
//        pendingLocker.updateReviewStatus(ReviewStatus.APPROVED);
//        pendingLocker.updateStatus(BaseStatus.ACTIVE);
//    }
//
//    @Override
//    public void reject(Long pendingLockerId, String reason){
//        PendingLocker pendingLocker = lockerReviewRepository.findById(pendingLockerId)
//                .orElseThrow(() -> new LockerException(NOT_FOUND_LOCKER));
//
//        LockerReview review = new LockerReview();
//        review.setPendingLocker(pendingLocker);
//        review.setReviewStatus(ReviewStatus.REJECTED);
//        review.setReviewComment(reason);
//
//        pendingLocker.setReviewComment(review);
//        pendingLocker.updateReviewStatus(ReviewStatus.REJECTED);
//    }



    // ================= 목록 + 페이징 =================
    @Override
    public Page<LockerReviewListResponse> findAllByStatus(ReviewStatus status, int page){
        Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Order.desc("createDate")));
        return lockerReviewRepository.findAllByStatus(status, pageable)
                .map(LockerReviewListResponse::from);
    }
}
