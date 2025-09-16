package com.airbng.admin.service;

import com.airbng.admin.domain.LockerReview;
import com.airbng.admin.domain.PendingLocker;
import com.airbng.admin.domain.PendingLockerImage;
import com.airbng.admin.domain.PendingLockerJimtype;
import com.airbng.admin.dto.response.LockerReviewDetailResponse;
import com.airbng.admin.repository.LockerReviewRepository;
import com.airbng.admin.repository.PendingLockerRepository;
import com.airbng.api.consumer.dto.command.LockerReviewDetailCommand;
import com.airbng.api.consumer.dto.view.LockerReviewDetailView;
import com.airbng.api.consumer.LockerApi;
import com.airbng.platform.common.exception.DomainException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.stream.Collectors;

import static com.airbng.platform.common.response.status.BaseResponseStatus.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LockerReviewServiceImpl implements LockerReviewService {

    private final PendingLockerRepository pendingLockerRepository;
    private final LockerReviewRepository lockerReviewRepository;
    private final LockerApi lockerApi;

    // ================= 상세 =================
    @Override
    public LockerReviewDetailResponse findLockerReviewById(Long lockerReviewId) {
        LockerReview lockerReview = lockerReviewRepository.findById(lockerReviewId)
                .orElseThrow(() -> new DomainException(NOT_FOUND_LOCKERDETAILS));
        PendingLocker pendingLocker = lockerReview.getPendingLocker();

        // User 데이터 조회
        LockerReviewDetailCommand command = LockerReviewDetailCommand.builder()
                .memberId(pendingLocker.getMemberId())
                .jimTypeIds(pendingLocker.getPendingLockerJimtypes().stream()
                        .map(PendingLockerJimtype::getJimtypeId)
                        .collect(Collectors.toList()))
                .imageIds(pendingLocker.getPendingLockerImages().stream()
                        .map(PendingLockerImage::getImageId)
                        .collect(Collectors.toList()))
                .build();

        LockerReviewDetailView userData = lockerApi.getLockerReviewDetail(command);

        LockerReviewDetailResponse lockerDetail = LockerReviewDetailResponse.from(pendingLocker, lockerReview, userData);
        return lockerDetail;
    }

}
