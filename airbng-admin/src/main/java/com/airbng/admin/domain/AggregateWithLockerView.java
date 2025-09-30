package com.airbng.admin.domain;

import com.airbng.common.base.LockerType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Subselect;
import org.hibernate.annotations.Synchronize;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Immutable
@Subselect("""
        SELECT
            a.aggregate_id,
            a.aggregate_date,
            a.total_sales,
            a.total_count,
            a.average_sales,
            a.total_fee,
            a.locker_id,
            l.locker_type
        FROM aggregate a
        LEFT JOIN locker l ON a.locker_id = l.locker_id
        """)
@Synchronize({"aggregate", "locker"})
@NoArgsConstructor
@Getter
@Table(name = "aggregate_with_locker_view")
public class AggregateWithLockerView {

    @Id
    @Column(name = "aggregate_id")
    private Long aggregateId;

    @Column(name = "locker_id")
    private Long lockerId;

    @Column(name = "aggregate_date")
    private LocalDateTime aggregateDate;

    @Column(name = "total_sales")
    private BigDecimal totalSales;

    @Column(name = "total_count")
    private Long totalCount;

    @Column(name = "average_sales")
    private BigDecimal averageSales;

    @Column(name = "total_fee")
    private BigDecimal totalFee;

    @Enumerated(EnumType.STRING)
    @Column(name = "locker_type")
    private LockerType lockerType;
}
