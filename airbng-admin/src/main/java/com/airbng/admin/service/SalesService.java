package com.airbng.admin.service;

import com.airbng.admin.dto.response.PeriodSalesResponse;
import com.airbng.admin.dto.response.StorageSalesResponse;
import com.airbng.common.base.LockerType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface SalesService {
    // 기간별 매출 통계 조회
    Page<PeriodSalesResponse> getPeriodSales(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    // 보관소별 매출 통계 조회
    List<StorageSalesResponse> getStorageSales(LockerType lockerType, LocalDateTime startDate, LocalDateTime endDate);
}
