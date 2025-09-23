package com.airbng.chat.service;

import com.airbng.chat.dto.ws.UserCardResponse;

import java.util.Collection;
import java.util.List;

public interface DirectoryService {
    List<UserCardResponse> getUserCards(Collection<Long> userIds);
    UserCardResponse getUserCard(long userId);
    UserCardResponse findByNickname(String nickname);

    /** 부분 검색 제안(닉네임/이름) */
    List<UserCardResponse> suggest(String q, int limit, Long excludeUserId);
}
