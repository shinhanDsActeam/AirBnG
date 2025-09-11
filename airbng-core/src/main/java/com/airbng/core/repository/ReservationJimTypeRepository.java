package com.airbng.core.repository;

import com.airbng.core.domain.base.BaseStatus;
import com.airbng.core.domain.jimtype.ReservationJimType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface ReservationJimTypeRepository extends JpaRepository<ReservationJimType, Long> {

    @Modifying
    @Query("UPDATE ReservationJimType rjt " +
            "SET rjt.status = :status " +
            "WHERE  rjt.reservation.reservationId = :reservationId")
    void updateStatusByReservationId(Long reservationId, BaseStatus status);

}
