package com.airbng.consumer.repository;

import com.airbng.common.base.BaseStatus;
import com.airbng.consumer.domain.jimtype.ReservationJimType;
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
