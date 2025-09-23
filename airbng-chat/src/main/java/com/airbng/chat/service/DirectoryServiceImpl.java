package com.airbng.chat.service;

import com.airbng.api.consumer.MemberApi;
import com.airbng.api.consumer.dto.view.MemberCardView;
import com.airbng.chat.dto.ws.UserCardResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DirectoryServiceImpl implements DirectoryService {

    // MemberApi로 바꿔야함
    private final MemberApi memberApi;
    private final PresenceService presenceService;

    @Override
    public List<UserCardResponse> getUserCards(Collection<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) return List.of();

        // 입력 순서 보존 + 중복 제거
        List<Long> ids = userIds.stream()
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());

        // 멤버 카드 조회(프로젝션)
        List<MemberCardView> rows = memberApi.findCardsByIds(ids);
        Map<Long, MemberCardView> cardMap = rows.stream()
                .collect(Collectors.toMap(MemberCardView::getMemberId, v -> v));

        // presence 병합
        Map<Long, Boolean> onlineMap = presenceService.isOnlineBulk(ids);
        Map<Long, Long> lastSeenMap = presenceService.lastSeenBulk(ids);

        List<UserCardResponse> out = new ArrayList<>(ids.size());
        for (Long id : ids) {
            MemberCardView v = cardMap.get(id);
            boolean online = Boolean.TRUE.equals(onlineMap.get(id));
            Long since = lastSeenMap.get(id);

            Integer sessions = online ? Math.toIntExact(presenceService.onlineSessionCount(id)) : null; // 온라인일 때만

            String name = v != null ? v.getName() : null;
            String nick = v != null ? v.getNickname() : null;
            String display = (nick != null && !nick.isBlank()) ? nick
                    : (name != null && !name.isBlank()) ? name : null;

            out.add(UserCardResponse.builder()
                    .id(id)
                    .name(v != null ? v.getName() : null)
                    .nickname(v != null ? v.getNickname() : null)
                    .imageUrl(v != null ? v.getImageUrl() : null)
                    .state(online ? "online" : "offline")
                    .since(since)
                    .online(online)
                    .sessions(sessions)
                    .displayName(display)
                    .build());
        }
        return out;
    }

    @Override
    public UserCardResponse getUserCard(long userId) {
        MemberCardView v = memberApi.findCardById(userId);
        boolean online = presenceService.isOnline(userId);
        Long since = presenceService.lastSeenAt(userId);
        Integer sessions = online ? Math.toIntExact(presenceService.onlineSessionCount(userId)) : null;

        String name = v != null ? v.getName() : null;
        String nick = v != null ? v.getNickname() : null;
        String display = (nick != null && !nick.isBlank()) ? nick
                : (name != null && !name.isBlank()) ? name : null;

        return UserCardResponse.builder()
                .id(userId)
                .name(name)
                .nickname(nick)
                .imageUrl(v != null ? v.getImageUrl() : null)
                .state(online ? "online" : "offline")
                .since(since)
                .online(online)
                .sessions(sessions)
                .displayName(display)
                .build();
    }

    @Override
    public UserCardResponse findByNickname(String nickname) {
        if (nickname == null || nickname.isBlank()) return null;
        MemberCardView v = memberApi.findCardByNickname(nickname.trim());
        if (v == null) return null;

        long id = v.getMemberId();
        boolean online = presenceService.isOnline(id);
        Long since = presenceService.lastSeenAt(id);
        Integer sessions = online ? Math.toIntExact(presenceService.onlineSessionCount(id)) : null;

        String name = v.getName();
        String nick = v.getNickname();
        String display = (nick != null && !nick.isBlank()) ? nick
                : (name != null && !name.isBlank()) ? name : null;

        return UserCardResponse.builder()
                .id(id)
                .name(name)
                .nickname(nick)
                .imageUrl(v.getImageUrl())
                .state(online ? "online" : "offline")
                .since(since)
                .online(online)
                .sessions(sessions)
                .displayName(display)
                .build();
    }

    @Override
    public List<UserCardResponse> suggest(String q, int limit, Long excludeUserId) {
        if (q == null || q.isBlank()) return List.of();
        var cards = memberApi.searchCards(q, limit + 5); // 여유로 가져와서 필터/정렬

        // 제외(본인 등)
        if (excludeUserId != null)
            cards = cards.stream().filter(c -> !excludeUserId.equals(c.getMemberId())).toList();

        // 랭킹: 닉네임 정확=0, 닉네임 prefix=1, 닉네임 contains=2, 이름 prefix=3, 이름 contains=4
        String s = q.toLowerCase();
        Comparator<MemberCardView> cmp = Comparator
                .comparingInt((MemberCardView v) -> {
                    String nn = Optional.ofNullable(v.getNickname()).orElse("").toLowerCase();
                    String nm = Optional.ofNullable(v.getName()).orElse("").toLowerCase();
                    if (nn.equals(s)) return 0;
                    if (nn.startsWith(s)) return 1;
                    if (nn.contains(s)) return 2;
                    if (nm.startsWith(s)) return 3;
                    return 4; // nm.contains(s) or fallback
                })
                .thenComparing(v -> Optional.ofNullable(v.getNickname()).orElse("~"))
                .thenComparing(v -> Optional.ofNullable(v.getName()).orElse("~"));

        var top = cards.stream().sorted(cmp).limit(Math.max(1, limit)).toList();

        // presence 합치기
        var ids = top.stream().map(MemberCardView::getMemberId).toList();
        var onlineMap = presenceService.isOnlineBulk(ids);
        var lastMap   = presenceService.lastSeenBulk(ids);

        return top.stream().map(v -> UserCardResponse.builder()
                .id(v.getMemberId())
                .name(v.getName())
                .nickname(v.getNickname())
                .imageUrl(v.getImageUrl())
                .state(Boolean.TRUE.equals(onlineMap.get(v.getMemberId())) ? "online" : "offline")
                .since(lastMap.get(v.getMemberId()))
                .build()
        ).collect(Collectors.toList());
    }
}
