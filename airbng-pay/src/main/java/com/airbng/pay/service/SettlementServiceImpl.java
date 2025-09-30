package com.airbng.pay.service;

import com.airbng.common.base.PaymentMethod;
import com.airbng.pay.domain.Settlement;
import com.airbng.pay.domain.view.CompletedReservationView;
import com.airbng.pay.repository.SettlementRepository;
import com.airbng.pay.repository.view.CompletedReservationViewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class SettlementServiceImpl implements SettlementService {

    private static final int PAGE_SIZE = 500;

    private final CompletedReservationViewRepository completedRepository;
    private final SettlementRepository settlementRepository;
    private final WalletService walletService;

    @Transactional
    public void processWindow(LocalDateTime from, LocalDateTime to) {
        int page = 0;
        long processed = 0;

        while (true) {
            Slice<CompletedReservationView> slice =
                    completedRepository.findWindow(from, to, PageRequest.of(page, PAGE_SIZE));
            if (slice.isEmpty()) break;

            for (CompletedReservationView v : slice) {
                if (settlementRepository.existsByReservationId(v.getReservationId())) {
                    continue; // 멱등
                }
                // 금전 이동 (keeper/system 보류 → 가용)
                walletService.performSettlement(v);

                Settlement entity = Settlement.builder()
                        .amount(v.getAmount())
                        .settlementDate(from) // 정산 대상 기간의 시작일
                        .reservationId(v.getReservationId())
                        .paymentMethod(PaymentMethod.WALLET) // TODO : 현재는 무조건 WALLET
                        .paymentFee(v.getFee())
                        .keeperId(v.getKeeperId())
                        .lockerId(v.getLockerId())
                        .createdAt(LocalDateTime.now())
                        .build();

                settlementRepository.save(entity);
                processed++;
            }

            if (!slice.hasNext()) break;
            page++;
        }
        log.info("[Settlement] {} ~ {} processed={}, pages={}", from, to, processed, page + 1);
    }
}