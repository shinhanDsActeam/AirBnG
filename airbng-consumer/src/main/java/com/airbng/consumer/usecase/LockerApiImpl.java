package com.airbng.consumer.usecase;

import com.airbng.api.consumer.dto.command.LockerReviewDetailCommand;
import com.airbng.api.consumer.dto.view.LockerReviewDetailView;
import com.airbng.api.consumer.LockerApi;
import com.airbng.consumer.domain.Member;
import com.airbng.api.consumer.dto.view.LockerJimTypeResult;
import com.airbng.consumer.domain.image.Image;
import com.airbng.consumer.exception.MemberException;
import com.airbng.consumer.repository.ImageRepository;
import com.airbng.consumer.repository.LockerJimTypeRepository;
import com.airbng.consumer.repository.MemberRepository;
import com.airbng.platform.common.response.status.BaseResponseStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class LockerApiImpl implements LockerApi {

    private final MemberRepository memberRepository;
    private final LockerJimTypeRepository jimTypeRepository;
    private final ImageRepository ImageRepository;


    @Override
    public LockerReviewDetailView getLockerReviewDetail(LockerReviewDetailCommand command) {
        // Member 조회
        Member member = memberRepository.findById(command.getMemberId())
                .orElseThrow(() -> new MemberException(BaseResponseStatus.NOT_FOUND_MEMBER));

        // JimType 조회
        List<LockerJimTypeResult> jimTypes = jimTypeRepository.findAllById(command.getJimTypeIds())
                .stream()
                .map(j -> new LockerJimTypeResult(
                        j.getJimType().getJimTypeId(),
                        j.getJimType().getTypeName(),
                        j.getJimType().getPricePerHour()
                ))
                .toList();

        // 이미지 조회
        List<String> imageUrls = ImageRepository.findAllById(command.getImageIds())
                .stream()
                .map(Image::getUrl)
                .toList();

        // DTO 반환
        return LockerReviewDetailView.builder()
                .memberName(member.getName())
                .memberPhone(member.getPhone())
                .jimTypes(jimTypes)
                .imageUrls(imageUrls)
                .build();
    }
}
