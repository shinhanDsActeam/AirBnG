package com.airbng.consumer.usecase;

import com.airbng.api.consumer.MemberApi;
import com.airbng.api.consumer.dto.view.MemberCardView;
import com.airbng.consumer.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
class MemberApiImpl implements MemberApi {

    private final MemberRepository memberRepository;

    @Override
    @Transactional(readOnly = true)
    public List<MemberCardView> findCardsByIds(Collection<Long> ids) {
        if (ids == null || ids.isEmpty()) return List.of();

        // null 제거 + 중복 제거
        List<Long> distinctIds = ids.stream()
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());

        if (distinctIds.isEmpty()) return List.of();

        return memberRepository.findCardsByIds(distinctIds);
    }

    @Override
    @Transactional(readOnly = true)
    public MemberCardView findCardByNickname(String nickname) {
        if (nickname == null || nickname.isBlank()) return null;
        return memberRepository.findCardByNickname(nickname.trim());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MemberCardView> searchCards(String keyword, int limit) {
        if (keyword == null || keyword.isBlank() || limit <= 0) return List.of();
        int lim = Math.max(1, Math.min(50, limit));
        return memberRepository.searchCards(keyword.trim(), PageRequest.of(0, lim));
    }

}
