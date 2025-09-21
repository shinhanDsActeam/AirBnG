package com.airbng.consumer.repository;

import com.airbng.consumer.domain.Locker;
import com.airbng.consumer.domain.Member;
import com.airbng.consumer.domain.Zzim;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ZzimRepository extends JpaRepository<Zzim, Long> {
    @Query("SELECT z  FROM Zzim z " +
            "WHERE z.locker.lockerId = :lockerId AND z.member.memberId = :memberId")
    Optional<Zzim> findByMemberIdAndLockerId(Long memberId, Long lockerId);
}
