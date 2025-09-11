package com.airbng.repository;

import com.airbng.domain.base.BaseStatus;
import com.airbng.domain.jimtype.ReservationJimType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ReservationJimTypeRepository extends JpaRepository<ReservationJimType, Long> {

    @Modifying
    @Query("UPDATE ReservationJimType rjt " +
            "SET rjt.status = :status " +
            "WHERE  rjt.reservation.reservationId = :reservationId")
    void updateStatusByReservationId(Long reservationId, BaseStatus status);

}
