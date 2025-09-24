package com.airbng.admin.dto.response;

import com.airbng.admin.domain.AggregateWithLockerView;
import com.airbng.common.base.LockerType;
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
    private LockerType lockerType;

    public static StorageSalesResponse from(AggregateWithLockerView view) {
        return StorageSalesResponse.builder()
                .aggregateId(view.getAggregateId())
                .updatedAt(view.getAggregateDate())
                .totalSales(view.getTotalSales())
                .totalCount(view.getTotalCount())
                .averageSales(view.getAverageSales())
                .totalFee(view.getTotalFee())
                .lockerId(view.getLockerId())
                .lockerType(view.getLockerType())
                .build();
    }
}
