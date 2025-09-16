package com.airbng.admin.service;

import com.airbng.admin.domain.LockerReview;
import com.airbng.admin.domain.PendingLocker;
import com.airbng.admin.domain.base.ReviewStatus;
import com.airbng.admin.dto.response.LockerReviewListResponse;
import com.airbng.admin.repository.LockerReviewRepository;
import com.airbng.api.consumer.LockerApi;
import com.airbng.api.consumer.dto.command.LockerReviewMemberCommand;
import com.airbng.api.consumer.dto.view.LockerReviewMemberView;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LockerReviewServiceImpl implements LockerReviewService{

    private final LockerReviewRepository lockerReviewRepository;
    private final LockerApi lockerApi;

    @Override
    @Transactional(readOnly = true)
    public Page<LockerReviewListResponse> findAllByReviewStatus(ReviewStatus status, int page) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Order.desc("createdAt")));

        Page<LockerReview> lockerReviews = lockerReviewRepository.findAllByReviewStatus(status, pageable);

        return lockerReviews.map(lockerReview -> {
            PendingLocker pendingLocker = lockerReview.getPendingLocker();

            LockerReviewMemberCommand command = LockerReviewMemberCommand.builder()
                    .memberId(pendingLocker.getMemberId())
                    .build();

            LockerReviewMemberView userData = lockerApi.getLockerReviewList(command);

            return LockerReviewListResponse.from(lockerReview, pendingLocker, userData);
        });

    }

}
