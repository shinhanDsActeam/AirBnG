package com.airbng.consumer.usecase;

import com.airbng.api.consumer.LockerApi;
import com.airbng.api.consumer.dto.command.LockerReviewMemberCommand;
import com.airbng.api.consumer.dto.view.LockerReviewMemberView;
import com.airbng.consumer.domain.Member;
import com.airbng.consumer.exception.MemberException;
import com.airbng.consumer.repository.MemberRepository;
import com.airbng.platform.common.response.status.BaseResponseStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class LockerApiImpl implements LockerApi {

    private final MemberRepository memberRepository;

    @Override
    public LockerReviewMemberView getLockerReviewList(LockerReviewMemberCommand command) {

        // Member 조회
        Member member = memberRepository.findById(command.getMemberId())
                .orElseThrow(() -> new MemberException(BaseResponseStatus.NOT_FOUND_MEMBER));

        return LockerReviewMemberView.builder()
                .memberName(member.getName())
                .build();

    }
}
