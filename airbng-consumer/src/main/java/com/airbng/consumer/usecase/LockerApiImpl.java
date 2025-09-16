package com.airbng.consumer.usecase;

import com.airbng.api.consumer.LockerApi;
import com.airbng.api.consumer.dto.command.LockerReviewApproveCommand;
import com.airbng.api.consumer.dto.command.LockerReviewRejectCommand;
import com.airbng.common.base.BaseStatus;
import com.airbng.consumer.domain.Locker;
import com.airbng.consumer.domain.Member;
import com.airbng.consumer.domain.image.Image;
import com.airbng.consumer.domain.image.LockerImage;
import com.airbng.consumer.domain.jimtype.JimType;
import com.airbng.consumer.domain.jimtype.LockerJimType;
import com.airbng.consumer.exception.ImageException;
import com.airbng.consumer.exception.LockerException;
import com.airbng.consumer.exception.MemberException;
import com.airbng.consumer.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.airbng.platform.common.response.status.BaseResponseStatus.*;

@Service
@RequiredArgsConstructor
public class LockerApiImpl implements LockerApi {

    private final LockerRepository lockerRepository;
    private final LockerImageRepository lockerImageRepository;
    private final LockerJimTypeRepository lockerJimTypeRepository;
    private final JimTypeRepository jimTypeRepository;
    private final MemberRepository memberRepository;
    private final ImageRepository imageRepository;

    @Override
    @Transactional
    public boolean createLockerFromPending(LockerReviewApproveCommand command) {

        // 1. 회원 조회
        Member keeper = memberRepository.findById(command.getMemberId())
                .orElseThrow(() -> new MemberException(NOT_FOUND_MEMBER));

        // 2. 실제 Locker 엔티티 생성
        Locker locker = Locker.builder()
                .lockerName(command.getLockerName())
                .address(command.getAddress())
                .addressEnglish(command.getAddressEnglish())
                .addressDetail(command.getAddressDetail())
                .latitude(command.getLatitude())
                .longitude(command.getLongitude())
                .keeper(keeper)
                .status(command.getStatus())
                .reservationCount(0L)
                .build();
        lockerRepository.saveAndFlush(locker);

        // 3. 이미지 저장/연결
        if (command.getImageId() != null && !command.getImageId().isEmpty()) {
            for (Long imageId : command.getImageId()) {
                Image image = imageRepository.findById(imageId)
                        .orElseThrow(() -> new ImageException("이미지 없음: " + imageId));

                lockerImageRepository.save(
                        LockerImage.builder()
                                .locker(locker)
                                .image(image)
                                .status(BaseStatus.ACTIVE)
                                .build()
                );
            }
        }

        // 4. JimType 연결
        if (command.getJimTypeId() != null && !command.getJimTypeId().isEmpty()) {
            List<JimType> types = jimTypeRepository.findAllById(command.getJimTypeId());
            for (JimType t : types) {
                lockerJimTypeRepository.save(
                        LockerJimType.builder()
                                .locker(locker)
                                .jimType(t)
                                .status(BaseStatus.ACTIVE)
                                .build()
                );
            }
        }

        return true;
    }

    @Override
    public LockerReviewRejectCommand rejectLockerReview(LockerReviewRejectCommand command) {

        // 클라이언트에 보여줄 DTO 생성
//        LockerReviewRejectCommand dto = LockerReviewRejectCommand.builder()
//                .lockerName(command.getLockerName())
//                .reason(command.getReason()) // command에 reason 필드가 있어야 함
//                .build();

        //TODO : 반려 사유를 어케 보여주지?
//        notificationService.sendRejectedLockerNotification(command.getMemberId(), dto);


        return new LockerReviewRejectCommand(
                command.getLockerName(),
                command.getReason() // LockerReviewRejectCommand에 reason 필드 있어야 함
        );

    }

}
