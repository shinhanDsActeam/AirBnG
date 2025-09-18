package com.airbng.admin.dto.response;

import com.airbng.common.domain.Aggregate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class StorageSalesResponse {
    private Long aggregateId;
    private LocalDateTime updatedAt;
    private BigDecimal totalSales;
    private Long totalCount;
    private BigDecimal averageSales;
    private BigDecimal totalFee;
    private Long lockerId;

    public static StorageSalesResponse from(Aggregate aggregate) {
        return StorageSalesResponse.builder()
                .aggregateId(aggregate.getAggregateId())
                .updatedAt(aggregate.getAggregateDate())
                .totalSales(aggregate.getTotalSales())
                .totalCount(aggregate.getTotalCount())
                .averageSales(aggregate.getAverageSales())
                .totalFee(aggregate.getTotalFee())
                .lockerId(aggregate.getLockerId())
                .build();
    }
}
