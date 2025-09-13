package com.airbng.admin.service;

import com.airbng.admin.common.exception.LockerException;
import com.airbng.admin.domain.base.ReviewStatus;
import com.airbng.admin.domain.review.LockerReview;
import com.airbng.admin.domain.review.PendingLocker;
import com.airbng.admin.dto.LockerReviewConfirmResponse;
import com.airbng.admin.dto.LockerReviewDetailResponse;
import com.airbng.admin.repository.LockerReviewRepository;
import com.github.benmanes.caffeine.cache.Cache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Pageable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import static com.airbng.admin.common.response.status.BaseResponseStatus.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LockerReviewServiceImpl implements LockerReviewService{

    private final LockerReviewRepository lockerReviewRepository;

    // ================= 상세 =================
    @Override
    public LockerReviewDetailResponse findLockerReviewById(Long lockerReviewId) {
        PendingLocker pendingLocker = lockerReviewRepository.findLockerReviewById(lockerReviewId)
                .orElseThrow(() -> new LockerException(NOT_FOUND_LOCKERDETAILS));

        LockerReview lr = pendingLocker.getReviewComment();

        return LockerReviewDetailResponse.from(pendingLocker);
    }


    private final Cache<Long, ReentrantLock> LockerReviewLocks;

    // ================= 승인/거절 =================
    @Override
    public LockerReviewConfirmResponse confirmLockerReviewState(Long lockerReviewId, String approve, Long memberId) {

        ReentrantLock lock = LockerReviewLocks.get(lockerReviewId, key -> new ReentrantLock());
        try {
            //락 걸어
            lock.lock();
//            //멤버 존재 유무 파악
//            if (!memberRepository.existsByMemberId(memberId)) throw new MemberException(NOT_FOUND_MEMBER);

            //예약건의 존재 여부 파악
            PendingLocker pendingLocker = lockerReviewRepository.findByLockerReviewId(lockerReviewId)
                    .orElseThrow(() -> new LockerException(NOT_FOUND_LOCKER));

            //존재하는 사람인지 확인
            if (!pendingLocker.getMemberId().equals(memberId))
                throw new LockerException(NOT_MEMBER_OF_LOCKER);

            //취소, 완료상태는 상태변경 불가
            pendingLocker.getReviewStatus().isAvailableUpdate(pendingLocker.getReviewStatus());
            /** 삭제 상태는 상태 변경 불가 */
            pendingLocker.isAvailableUpdateState();
            //상태값 저장
            ReviewStatus newState;
            String notificationMessage;

            if ("yes".equalsIgnoreCase(approve)) {
                newState = ReviewStatus.APPROVED;

                notificationMessage = "예약이 확정되었습니다.";

            } else if ("no".equalsIgnoreCase(approve)) {
                newState = ReviewStatus.REJECTED;

                notificationMessage = "예약이 거절되었습니다.";

            } else {
                throw new LockerException(CANNOT_UPDATE_STATE);
            }
            /** 더티 체킹으로 대체 */
            ;

//알림 발송 -> 일단 주석
//            if (pendingLocker != null) {
//                NotificationType notificationType = newState == ReviewStatus.WAITING ?
//                        NotificationType.STATE_CHANGE : NotificationType.CANCEL_NOTICE;
//                log.info("알림 발송: memberId={}, reservationId={}, nickname={}, role=DROPPER, type={}, message={}",
//                        pendingLocker.getMemberId(), lockerReviewId,
//                        pendingLocker.getMemberName(), notificationType, notificationMessage);
//                ;
//
//                alertScheduledTask.sendToOne(
//                        pendingLocker.getMemberId(),
//                        lockerReviewId,
//                        pendingLocker.getNickname(),
//                        "DROPPER",
//                        notificationType,
//                        notificationMessage
//                );
//            }

            return LockerReviewConfirmResponse.of(pendingLocker, newState);
        } finally {
            lock.unlock();
        }

    }

    // ================= 목록 + 페이징 =================
    @Override
    public Page<PendingLocker> findAll(int page){
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createDate"));
        Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));
        return lockerReviewRepository.findAll(pageable);
    }
}
