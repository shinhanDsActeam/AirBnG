package com.airbng.admin.service;

import com.airbng.admin.dto.response.PeriodSalesResponse;
import com.airbng.admin.dto.response.StorageSalesResponse;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SalesService {
    // 기간별 매출 통계 조회
    List<PeriodSalesResponse> getPeriodSales(LocalDateTime startDate, LocalDateTime endDate);

    // 보관소별 매출 통계 조회
    Optional<StorageSalesResponse> getStorageSales(Long lockerId, LocalDateTime startDate, LocalDateTime endDate);
}
