package com.airbng.admin.service;

import com.airbng.admin.dto.response.PeriodSalesResponse;
import com.airbng.admin.dto.response.StorageSalesResponse;
import com.airbng.admin.exception.SalesException;
import com.airbng.common.base.LockerType;
import com.airbng.admin.domain.AggregateWithLockerView;
import com.airbng.common.domain.Settlement;
import com.airbng.admin.repository.AggregateWithLockerViewRepository;
import com.airbng.common.repository.SettlementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static com.airbng.platform.common.response.status.BaseResponseStatus.NOT_FOUND_PERIOD_SALES;
import static com.airbng.platform.common.response.status.BaseResponseStatus.NOT_FOUND_STORAGE_SALES;

@Service
@RequiredArgsConstructor
public class SalesServiceImpl implements SalesService {

    private final SettlementRepository settlementRepository;
    private final AggregateWithLockerViewRepository aggregateWithLockerViewRepository;

    @Transactional(readOnly = true)
    @Override
    public Page<PeriodSalesResponse> getPeriodSales(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        if (startDate.isAfter(LocalDateTime.now())) throw new SalesException(NOT_FOUND_PERIOD_SALES);
        Page<Settlement> settlement = settlementRepository.findByPeriodSales(startDate, endDate, pageable);
        if (settlement == null || settlement.isEmpty()) throw new SalesException(NOT_FOUND_PERIOD_SALES);

        return settlement.map(PeriodSalesResponse::from);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<StorageSalesResponse> getStorageSales(
            LockerType lockerType, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        Page<AggregateWithLockerView> aggregate = aggregateWithLockerViewRepository.findByStorageSales(
                lockerType.name(), startDate, endDate, pageable);
//        if (aggregate == null || aggregate.isEmpty()) throw new SalesException(NOT_FOUND_STORAGE_SALES);

        return aggregate.map(StorageSalesResponse::from);
    }
}
