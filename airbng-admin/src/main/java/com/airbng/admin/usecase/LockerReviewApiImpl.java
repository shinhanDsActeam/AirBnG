package com.airbng.admin.usecase;

import com.airbng.admin.domain.LockerReview;
import com.airbng.admin.domain.PendingLocker;
import com.airbng.admin.domain.PendingLockerImage;
import com.airbng.admin.domain.PendingLockerJimtype;
import com.airbng.admin.domain.base.ReviewStatus;
import com.airbng.admin.repository.LockerReviewImageRepository;
import com.airbng.admin.repository.LockerReviewJimTypeRepository;
import com.airbng.admin.repository.LockerReviewRepository;
import com.airbng.admin.repository.PendingLockerRepository;
import com.airbng.api.admin.LockerReviewApi;
import com.airbng.api.admin.dto.command.LockerReviewCommand;
import com.airbng.api.admin.dto.view.LockerViewStatusView;
import com.airbng.common.base.BaseStatus;
import com.airbng.common.base.LockerType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LockerReviewApiImpl implements LockerReviewApi {

    private final LockerReviewImageRepository lockerReviewImageRepository;
    private final LockerReviewJimTypeRepository lockerreviewJimTypeRepository;
    private final LockerReviewRepository lockerReviewRepository;
    private final PendingLockerRepository pendingLockerRepository;


    @Override
    @Transactional
    public boolean submitLockerForReview(LockerReviewCommand dto) {
        // 1) PendingLocker 생성
        PendingLocker pendingLocker = PendingLocker.builder()
                .lockerName(dto.getLockerName())
                .address(dto.getAddress())
                .addressEnglish(dto.getAddressEnglish())
                .addressDetail(dto.getAddressDetail())
                .latitude(dto.getLatitude())
                .longitude(dto.getLongitude())
                .memberId(dto.getMemberId())
                .lockerType(LockerType.valueOf(dto.getLockerType()))
                .status(BaseStatus.ACTIVE)
                .build();

        pendingLockerRepository.save(pendingLocker);

        // 2) 초기 심사 상태 (WAITING) 세팅
        LockerReview review = LockerReview.builder()
                .reviewStatus(ReviewStatus.WAITING)
                .status(BaseStatus.ACTIVE)
                .pendingLocker(pendingLocker)
                .build();


        lockerReviewRepository.save(review);


        // 3) 이미지 연결
        if (dto.getImageId() != null && !dto.getImageId().isEmpty()) {
            for (Long imageId : dto.getImageId()) {
                PendingLockerImage pli = PendingLockerImage.builder()
                        .pendingLocker(pendingLocker)
                        .imageId(imageId)
                        .status(BaseStatus.ACTIVE)
                        .build();

                lockerReviewImageRepository.save(pli);
            }
        }

        // 5) JimType 연결 (유저가 보낸 리스트 기반)
        if (dto.getJimTypeId() != null && !dto.getJimTypeId().isEmpty()) {
            for (Long jimTypeId : dto.getJimTypeId()) {
                lockerreviewJimTypeRepository.save(
                        PendingLockerJimtype.builder()
                                .pendingLocker(pendingLocker)
                                .jimtypeId(jimTypeId)
                                .status(BaseStatus.ACTIVE)
                                .build()
                );
            }
        }

        return true;
    }


    @Override
    public LockerViewStatusView getLockerStatusByMemberId(Long memberId) {

        // 가장 최근 심사 기록 조회 (반려/대기/승인)
        LockerReview review = lockerReviewRepository.findTopByPendingLocker_MemberIdOrderByCreatedAtDesc(memberId)
                .orElse(null);

        if(review == null) {
            // 심사 기록 없음 → 등록 가능
            return LockerViewStatusView.builder()
                    .reviewStatus("REGISTER")
                    .build();
        }

        // 심사 상태 확인
        String status;
        switch(review.getReviewStatus()) {
            case APPROVED:
                status = "APPROVED";   // 이미 승인된 보관소 있음 → 버튼 비활성
                break;
            case WAITING:
                status = "WAITING";    // 심사 중 → 버튼 비활성
                break;
            case REJECTED:
                status = "REGISTER";   // 반려됨 → 다시 등록 가능
                break;
            default:
                status = "REGISTER";
        }

        return LockerViewStatusView.builder()
                .reviewStatus(status)
                .build();
    }

}
