package com.airbng.api.consumer;

import com.airbng.api.consumer.dto.view.MemberCardView;

import java.util.Collection;
import java.util.List;

public interface MemberApi {
    List<MemberCardView> findCardsByIds(Collection<Long> ids);

    /** 닉네임 정확 일치(대소문자 무시) */
    MemberCardView findCardByNickname(String nickname);

    /** 닉네임/이름 부분 일치(대소문자 무시), 최대 limit개 */
    List<MemberCardView> searchCards(String keyword, int limit);

    default MemberCardView findCardById(long id) {
        var list = findCardsByIds(List.of(id));
        return list.isEmpty() ? null : list.get(0);
    }
}
