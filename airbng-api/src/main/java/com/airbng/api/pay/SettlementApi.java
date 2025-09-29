package com.airbng.api.pay;

import com.airbng.api.pay.dto.view.PeriodSalesView;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface SettlementApi {

    Page<PeriodSalesView> findByPeriodSales(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

}
