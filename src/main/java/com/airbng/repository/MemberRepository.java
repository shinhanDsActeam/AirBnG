package com.airbng.repository;

import com.airbng.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;


public interface MemberRepository extends JpaRepository<Member, Long> {

    boolean existsByMemberId(Long memberId);

    boolean existsByNickname(String nickname);
}
