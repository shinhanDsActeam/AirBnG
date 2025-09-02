package com.airbng.repository;

import com.airbng.domain.Locker;
import com.airbng.domain.base.ReservationState;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LockerRepository extends JpaRepository<Locker, Long> {

    // 상세 조회 (N+1 방지용 fetch join)
    @EntityGraph(attributePaths = { "keeper", "lockerImages.image", "lockerJimTypes.jimType" })
    @Query("select l from Locker l where l.lockerId = :lockerId")
    Optional<Locker> findLockerById(@Param("lockerId") Long lockerId);

    // 내 보관소 상세 (memberId로)
    @EntityGraph(attributePaths = { "keeper", "lockerImages.image", "lockerJimTypes.jimType" })
    @Query("select l from Locker l where l.keeper.memberId = :memberId")
    Optional<Locker> findMyLocker(@Param("memberId") Long memberId);

    // 동적 검색 (address / lockerName / jimTypeIds) - 목록
    @EntityGraph(attributePaths = { "keeper" }) // 목록에선 keeper만 즉시 로딩
    @Query("""
        select distinct l
        from Locker l
        left join l.lockerJimTypes lj
        left join lj.jimType j
        where (:address is null or :address = '' or l.address like concat('%', :address, '%'))
          and (:lockerName is null or :lockerName = '' or l.lockerName like concat('%', :lockerName, '%'))
          and (:emptyJimTypes = true or j.jimTypeId in :jimTypeIds)
        order by l.lockerId asc
    """)
    List<Locker> searchForList(@Param("address") String address,
                               @Param("lockerName") String lockerName,
                               @Param("jimTypeIds") List<Long> jimTypeIds,
                               @Param("emptyJimTypes") boolean emptyJimTypes);

    // 동적 검색 카운트
    @Query("""
        select count(distinct l.lockerId)
        from Locker l
        left join l.lockerJimTypes lj
        left join lj.jimType j
        where (:address is null or :address = '' or l.address like concat('%', :address, '%'))
          and (:lockerName is null or :lockerName = '' or l.lockerName like concat('%', :lockerName, '%'))
          and (:emptyJimTypes = true or j.jimTypeId in :jimTypeIds)
    """)
    long searchCount(@Param("address") String address,
                     @Param("lockerName") String lockerName,
                     @Param("jimTypeIds") List<Long> jimTypeIds,
                     @Param("emptyJimTypes") boolean emptyJimTypes);

    // Top5 (예약 수 기준 정렬) — Pageable로 limit 대체
    @Query("""
        SELECT DISTINCT l FROM Locker l
        JOIN FETCH l.keeper k
        LEFT JOIN FETCH l.lockerImages li
        LEFT JOIN FETCH li.image i
        LEFT JOIN FETCH l.lockerJimTypes lj
        JOIN FETCH lj.jimType j
        WHERE EXISTS (
            SELECT r FROM Reservation r
            WHERE r.keeper = k AND r.state = :state
        )
        ORDER BY l.reservationCount DESC
        limit 5
    """)
    List<Locker> findTop5LockersByReservation(@Param("state") ReservationState state);

    // keeper가 보관소 보유 여부
    boolean existsByKeeper_MemberId(Long memberId);

    // 조인 테이블 정리
    @Modifying
    @Query("delete from LockerImage li where li.locker.lockerId = :lockerId")
    int deleteLockerImagesByLockerId(@Param("lockerId") Long lockerId);

    @Modifying
    @Query("delete from LockerJimType lj where lj.locker.lockerId = :lockerId")
    int deleteLockerJimTypesByLockerId(@Param("lockerId") Long lockerId);
}
