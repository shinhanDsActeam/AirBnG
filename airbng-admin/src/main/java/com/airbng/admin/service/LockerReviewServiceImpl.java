package com.airbng.admin.service;

import com.airbng.admin.domain.LockerReview;
import com.airbng.admin.domain.PendingLocker;
import com.airbng.admin.domain.PendingLockerImage;
import com.airbng.admin.domain.PendingLockerJimtype;
import com.airbng.admin.domain.base.ReviewStatus;
import com.airbng.admin.dto.response.LockerReviewDetailResponse;
import com.airbng.admin.repository.LockerReviewRepository;
import com.airbng.api.consumer.event.LockerRejectedEvent;
import com.airbng.api.consumer.AlarmApi;
import com.airbng.api.consumer.dto.command.LockerReviewApproveCommand;
import com.airbng.api.consumer.dto.command.LockerReviewDetailCommand;
import com.airbng.api.consumer.dto.command.LockerReviewMemberCommand;
import com.airbng.api.consumer.dto.command.LockerReviewRejectCommand;
import com.airbng.api.consumer.dto.view.LockerReviewDetailView;
import com.airbng.api.consumer.LockerApi;
import com.airbng.api.consumer.dto.view.LockerReviewMemberView;
import com.airbng.platform.common.exception.DomainException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.stream.Collectors;
import com.airbng.admin.dto.response.LockerReviewListResponse;
import org.springframework.data.domain.*;

import static com.airbng.platform.common.response.status.BaseResponseStatus.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LockerReviewServiceImpl implements LockerReviewService {

    private final ApplicationEventPublisher eventPublisher;
    private final LockerReviewRepository lockerReviewRepository;
    private final LockerApi lockerApi;
    private final AlarmApi alarmApi;

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

     @Override
    public Page<LockerReviewListResponse> findAllByReviewStatus(ReviewStatus status, int page) {
         int pageNumber = page - 1;
         Pageable pageable = PageRequest.of(pageNumber, 5, Sort.by(Sort.Order.desc("createdAt")));

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




    @Transactional
    public boolean approveLockerReview(Long lockerReviewId, Long memberId) {

        LockerReview lockerReview = lockerReviewRepository.findById(lockerReviewId)
                .orElseThrow(() -> new DomainException(NOT_FOUND_LOCKERDETAILS));

        // 상태 업데이트
        lockerReview.updateState(ReviewStatus.APPROVED);

        // User 데이터 조회
        LockerReviewMemberCommand commandMember = LockerReviewMemberCommand.builder()
                .memberId(lockerReview.getPendingLocker().getMemberId())
                .build();

        LockerReviewMemberView userData = lockerApi.getLockerReviewList(commandMember);
        String memberName = userData.getMemberName(); // 이름 가져오기



        // PendingLocker -> DTO 변환
        LockerReviewApproveCommand command = LockerReviewApproveCommand.builder()
                .memberId(memberId)
                .lockerName(lockerReview.getPendingLocker().getLockerName())
                .address(lockerReview.getPendingLocker().getAddress())
                .addressEnglish(lockerReview.getPendingLocker().getAddressEnglish())
                .addressDetail(lockerReview.getPendingLocker().getAddressDetail())
                .latitude(lockerReview.getPendingLocker().getLatitude())
                .longitude(lockerReview.getPendingLocker().getLongitude())
                .lockerType(lockerReview.getPendingLocker().getLockerType().toString())
                .jimTypeId(lockerReview.getPendingLocker().getPendingLockerJimtypes().stream()
                        .map(PendingLockerJimtype::getJimtypeId)
                        .toList())
                .imageId(lockerReview.getPendingLocker().getPendingLockerImages().stream()
                        .map(PendingLockerImage::getImageId)
                        .toList())
                .status(lockerReview.getPendingLocker().getStatus())
                .zzimCount(0L)
                .build();

        // 유저 서버에 전달
        boolean result =  lockerApi.createLockerFromPending(command);
        // 알림 전송
        try {
            alarmApi.sendLockerApproved(memberId, memberName, lockerReview.getPendingLocker().getLockerName(), lockerReview.getPendingLocker().getPendingLockerId());
        } catch (Exception e) {
            log.error("알림 발송 실패: memberId={}, lockerName={}", memberId, lockerReview.getPendingLocker().getLockerName(), e);
        }


        return result;
    }

    @Transactional
    public boolean rejectLockerReview(Long lockerReviewId, Long memberId, String reason) {

        LockerReview lockerReview = lockerReviewRepository.findById(lockerReviewId)
                .orElseThrow(() -> new DomainException(NOT_FOUND_LOCKERDETAILS));

        // 상태 업데이트
        lockerReview.updateState(ReviewStatus.REJECTED);
        lockerReview.updateComment(reason);
        // User 데이터 조회
        LockerReviewMemberCommand commandMember = LockerReviewMemberCommand.builder()
                .memberId(lockerReview.getPendingLocker().getMemberId())
                .build();

        LockerReviewMemberView userData = lockerApi.getLockerReviewList(commandMember);
        String memberName = userData.getMemberName(); // 이름 가져오기


        // PendingLocker -> DTO 변환
        LockerReviewRejectCommand command = LockerReviewRejectCommand.builder()
                .memberId(memberId)
                .lockerName(lockerReview.getPendingLocker().getLockerName())
                .reason(lockerReview.getReviewComment())
                .build();

        // 유저 서버에 전달
        boolean result =  lockerApi.rejectLockerReview(command);
        // 알림 전송
        try {
            alarmApi.sendLockerRejected(memberId, memberName, lockerReview.getPendingLocker().getLockerName(),reason);
        } catch (Exception e) {
            log.error("알림 발송 실패: memberId={}, lockerName={}", memberId, lockerReview.getPendingLocker().getLockerName(), e);
        }

        return result;
    }

}
