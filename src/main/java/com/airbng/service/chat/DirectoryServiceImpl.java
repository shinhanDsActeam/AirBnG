package com.airbng.service.chat;

import com.airbng.dto.ws.UserCardResponse;
import com.airbng.repository.MemberRepository;
import com.airbng.repository.chat.MemberCardView;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DirectoryServiceImpl implements DirectoryService {

    private final MemberRepository memberRepository;

    @Override
    public List<UserCardResponse> getUserCards(Collection<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) return List.of();
        List<MemberCardView> rows = memberRepository.findCardsByIds(userIds);
        return rows.stream()
                .map(v -> UserCardResponse.builder()
                        .id(v.getMemberId())
                        .name(v.getName())
                        .nickname(v.getNickname())
                        .imageUrl(v.getImageUrl())
                        .state("online")
                        .since(null) // presence:since를 Redis에 저장하면 여기로 채워줘도 됨
                        .build()
                )
                .toList();
    }
}
