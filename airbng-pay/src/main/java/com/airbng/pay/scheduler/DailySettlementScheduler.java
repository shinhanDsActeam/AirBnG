package com.airbng.pay.scheduler;

import com.airbng.pay.service.AggregateService;
import com.airbng.pay.service.RefundProcessingService;
import com.airbng.pay.service.SettlementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Slf4j
@Component
@RequiredArgsConstructor
public class DailySettlementScheduler {

    private final SettlementService settlementService;
    private final RefundProcessingService refundService;
    private final AggregateService aggregateService;

    // 매일 00:00:05 KST
    @Scheduled(cron = "5 0 0 * * *", zone = "Asia/Seoul")
    public void runDaily() {
        ZoneId zone = ZoneId.of("Asia/Seoul");
        LocalDate today = LocalDate.now(zone);
        LocalDateTime from = today.minusDays(1).atStartOfDay(zone).toLocalDateTime(); // [D-1 00:00
        LocalDateTime to   = today.atStartOfDay(zone).toLocalDateTime();              //  D   00:00)

            log.info("[Daily] start window=[{} ~ {})", from, to);

        settlementService.processWindow(from, to);
        refundService.processWindow(from, to);
        aggregateService.fillForDate(from, to);

    }
}