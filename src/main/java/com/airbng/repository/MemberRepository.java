package com.airbng.repository;

import com.airbng.domain.Member;
import com.airbng.repository.chat.MemberCardView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    boolean existsByEmail(String email);

    boolean existsByNickname(String nickname);

    boolean existsByPhone(String phone);

    boolean existsByMemberId(Long memberId);

    Member findMemberByEmail(String email);

    Optional<Member> findByEmail(String email);

    // TODO(modularize): 채팅 시작할 때 상대방 정보 조회용 (MemberCardView)
    @Query("""
      select m.memberId as memberId,
             m.name as name,
             m.nickname as nickname,
             i.url as imageUrl
      from Member m
      left join m.profileImage i
      where m.memberId in :ids
    """)
    List<MemberCardView> findCardsByIds(@Param("ids") Collection<Long> ids);
}
