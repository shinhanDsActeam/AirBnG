package com.airbng.admin.service;

import com.airbng.admin.dto.response.PeriodSalesResponse;
import com.airbng.admin.dto.response.StorageSalesResponse;
import com.airbng.admin.exception.SalesException;
import com.airbng.common.domain.Aggregate;
import com.airbng.common.domain.Settlement;
import com.airbng.common.repository.AggregateRepository;
import com.airbng.common.repository.SettlementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

import static com.airbng.platform.common.response.status.BaseResponseStatus.NOT_FOUND_PERIOD_SALES;
import static com.airbng.platform.common.response.status.BaseResponseStatus.NOT_FOUND_STORAGE_SALES;

@Service
@RequiredArgsConstructor
public class SalesServiceImpl implements SalesService {

    private final SettlementRepository settlementRepository;
    private final AggregateRepository aggregateRepository;

    @Transactional(readOnly = true)
    @Override
    public Optional<PeriodSalesResponse> getPeriodSales(LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate.isAfter(LocalDateTime.now())) throw new SalesException(NOT_FOUND_PERIOD_SALES);
        Settlement settlement = settlementRepository.findByPeriodSales(startDate, endDate)
                .orElseThrow(() -> new SalesException(NOT_FOUND_PERIOD_SALES));

        return Optional.of(PeriodSalesResponse.from(settlement));
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<StorageSalesResponse> getStorageSales(Long lockerId, LocalDateTime startDate, LocalDateTime endDate) {
        if (lockerId == null || lockerId <= 0) throw new SalesException(NOT_FOUND_STORAGE_SALES);
        Aggregate aggregate = aggregateRepository.findByStorageSales(lockerId, startDate, endDate)
                .orElseThrow(() -> new SalesException(NOT_FOUND_STORAGE_SALES));

        return Optional.of(StorageSalesResponse.from(aggregate));
    }

}
