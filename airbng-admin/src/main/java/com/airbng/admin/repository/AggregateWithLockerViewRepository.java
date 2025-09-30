package com.airbng.admin.repository;

import com.airbng.admin.domain.AggregateWithLockerView;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface AggregateWithLockerViewRepository extends JpaRepository<AggregateWithLockerView, Long> {
    @Query(value = "SELECT * FROM aggregate_with_locker_view v " +
            "WHERE v.locker_type = :lockerType "
//            "AND v.aggregate_date BETWEEN :startDate AND :endDate",
            ,
            countQuery = "SELECT COUNT(*) FROM aggregate_with_locker_view v " +
                    "WHERE v.locker_type = :lockerType ",
//                    "AND v.aggregate_date BETWEEN :startDate AND :endDate",
            nativeQuery = true)
    Page<AggregateWithLockerView> findByStorageSales(
            @Param("lockerType") String lockerType,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable
    );
}