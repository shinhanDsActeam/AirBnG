package com.airbng.consumer.usecase;

import com.airbng.api.consumer.dto.command.LockerReviewDetailCommand;
import com.airbng.api.consumer.dto.view.LockerReviewDetailView;
import com.airbng.api.consumer.LockerApi;
import com.airbng.api.consumer.dto.command.LockerReviewMemberCommand;
import com.airbng.api.consumer.dto.view.LockerReviewMemberView;
import com.airbng.consumer.domain.Member;
import com.airbng.consumer.exception.MemberException;
import com.airbng.consumer.domain.image.Image;
import com.airbng.consumer.repository.ImageRepository;
import com.airbng.consumer.repository.JimTypeRepository;
import com.airbng.consumer.repository.LockerJimTypeRepository;
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
    private final JimTypeRepository jimTypeRepository;
    private final ImageRepository imageRepository;

    // --- getLockerReviewList ---
    @Override
    public LockerReviewMemberView getLockerReviewList(LockerReviewMemberCommand command) {
        Member member = memberRepository.findById(command.getMemberId())
                .orElseThrow(() -> new MemberException(BaseResponseStatus.NOT_FOUND_MEMBER));

        return LockerReviewMemberView.builder()
                .memberName(member.getName())
                .build();
    }

    // --- getLockerReviewDetail ---
    @Override
    public LockerReviewDetailView getLockerReviewDetail(LockerReviewDetailCommand command) {
        Member member = memberRepository.findById(command.getMemberId())
                .orElseThrow(() -> new MemberException(BaseResponseStatus.NOT_FOUND_MEMBER));

        // JimType 조회
        List<LockerJimTypeResult> jimTypes = jimTypeRepository.findAllById(command.getJimTypeIds())
                .stream()
                .map(j -> new LockerJimTypeResult(
                        j.getJimTypeId(),
                        j.getTypeName(),
                        j.getPricePerHour()
                ))
                .toList();

        // 이미지 조회
        List<String> imageUrls = imageRepository.findAllById(command.getImageIds())
                .stream()
                .map(Image::getUrl)
                .toList();

        return LockerReviewDetailView.builder()
                .memberName(member.getName())
                .memberPhone(member.getPhone())
                .jimTypes(jimTypes)
                .imageUrls(imageUrls)
                .build();
    }
}
