package com.airbng.pay.service;

import com.airbng.pay.domain.Aggregate;
import com.airbng.pay.dto.AggByLocker;
import com.airbng.pay.dto.AggTotal;
import com.airbng.pay.repository.AggregateRepository;
import com.airbng.pay.repository.SettlementRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AggregateServiceImpl implements AggregateService {


    private final SettlementRepository settlementRepository;
    private final AggregateRepository aggregateRepository;

    @Transactional
    public void fillForDate(LocalDateTime from, LocalDateTime to) {
        // 집계 키(행)로 쓸 날짜= targetDate 00:00 (KST)
        // 소스 윈도우 = [targetDate 00:00, targetDate+1 00:00)
        // 1) 보관소별
        List<AggByLocker> rows = settlementRepository.aggregateByLocker(from, to);
        for (AggByLocker r : rows) {
            log.info("ByLocker aggregate: {}", r);
            upsert(from, r.lockerId(), r.totalSales(), r.totalCount(), r.totalFee());
        }

        // 2) 전체(옵션) — lockerId=0 으로 저장
        AggTotal t = settlementRepository.aggregateTotal(from, to);
        log.info("Total aggregate: {}", t);

        upsert(from, null, t.totalSales(), t.totalCount(), t.totalFee());
    }

    private void upsert(LocalDateTime aggregateDate,
                        Long lockerId,
                        BigDecimal totalSales,
                        Long totalCount,
                        BigDecimal totalFee) {

        Aggregate row = aggregateRepository
                .findByAggregateDateAndLockerId(aggregateDate, lockerId)
                .orElseGet(() -> Aggregate.builder()
                        .aggregateDate(aggregateDate)
                        .lockerId(lockerId)
                        .build());

        BigDecimal safeSales = totalSales != null ? totalSales : BigDecimal.ZERO;
        BigDecimal safeFee = totalFee != null ? totalFee : BigDecimal.ZERO;
        Long safeCount = totalCount != null ? totalCount : 0L;

        row.setTotalSales(safeSales);
        row.setTotalFee(safeFee);
        row.setTotalCount(safeCount);
        row.setAverageSales(safeCount == 0L ? BigDecimal.ZERO
                : safeSales.divide(BigDecimal.valueOf(safeCount), 2, RoundingMode.HALF_UP));

        aggregateRepository.save(row);
    }
}