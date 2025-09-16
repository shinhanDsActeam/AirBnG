package com.airbng.consumer.usecase;

import com.airbng.api.consumer.dto.command.LockerReviewDetailCommand;
import com.airbng.api.consumer.dto.view.LockerReviewDetailView;
import com.airbng.api.consumer.LockerApi;
import com.airbng.consumer.domain.Member;
import com.airbng.api.consumer.dto.view.LockerJimTypeResult;
import com.airbng.consumer.repository.LockerImageRepository;
import com.airbng.consumer.repository.LockerJimTypeRepository;
import com.airbng.consumer.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LockerApiImpl implements LockerApi {

    private final MemberRepository memberRepository;
    private final LockerJimTypeRepository jimTypeRepository;
    private final LockerImageRepository lockerImageRepository;


    @Override
    public LockerReviewDetailView getLockerReviewDetail(LockerReviewDetailCommand command) {
        // Member 조회
        Member member = memberRepository.findById(command.getMemberId())
                .orElseThrow(() -> new RuntimeException("회원 없음"));

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
        List<String> imageUrls = lockerImageRepository.findAllById(command.getImageIds())
                .stream()
                .map(t -> t.getImage().getUrl())
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
