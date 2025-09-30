package com.airbng.pay.usecase;

import com.airbng.api.pay.SettlementApi;
import com.airbng.api.pay.dto.view.PeriodSalesView;
import com.airbng.pay.domain.Settlement;
import com.airbng.pay.repository.SettlementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class SettlementApiImpl implements SettlementApi {
//
//    @Override
//    public void settleOne(SettlementCommand cmd) {
//
//    }
    private final SettlementRepository settlementRepository;

    @Override
    public Page<PeriodSalesView> findByPeriodSales(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        Page<Settlement> settlements = settlementRepository.findByPeriodSales(startDate, endDate, pageable);
        return settlements.map(this::toPeriodSalesView);
    }

    private PeriodSalesView toPeriodSalesView(Settlement settlement) {
        return PeriodSalesView.builder()
                .settlementId(settlement.getSettlementId())
                .amount(settlement.getAmount())
                .settlementDate(settlement.getSettlementDate())
                .paymentMethod(settlement.getPaymentMethod())
                .paymentFee(settlement.getPaymentFee())
                .reservationId(settlement.getReservationId())
                .keeperId(settlement.getKeeperId())
                .createdAt(settlement.getCreatedAt())
                .build();
    }
}
