package com.airbng.core.repository;

import com.airbng.core.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    boolean existsByEmail(String email);

    boolean existsByNickname(String nickname);

    boolean existsByPhone(String phone);

    boolean existsByMemberId(Long memberId);

    Member findMemberByEmail(String email);

    Optional<Member> findByEmail(String email);
}
