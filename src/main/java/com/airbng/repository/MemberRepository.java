package com.airbng.repository;

import com.airbng.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    boolean existsByMemberId(Long memberId);

    boolean existsByNickname(String nickname);

    boolean existsByEmail(String email);

    boolean existsByPhone(String phone);

    Member findMemberByEmail(String email);
}
