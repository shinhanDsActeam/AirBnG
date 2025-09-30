package com.airbng.pay.service;

import com.airbng.pay.domain.Refund;
import com.airbng.pay.repository.RefundRepository;
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
public class RefundProcessingServiceImpl implements RefundProcessingService {

    private static final int PAGE_SIZE = 500;

    private final RefundRepository refundRepository;
    private final WalletService walletService;

    @Transactional
    public void processWindow(LocalDateTime from, LocalDateTime to) {
        int page = 0;
        long processed = 0;

        while (true) {
            Slice<Refund> slice =
                    refundRepository.findWindow(from, to, PageRequest.of(page, PAGE_SIZE));
            if (slice.isEmpty()) break;

            for (Refund refund : slice) {
                // 멱등: 이미 처리된 건 스킵
                if (refund.isProcessed()) continue;

                // 금전 이동
                walletService.performRefund(refund);

                // 상태 전이
                refund.markProcessedNow();
                processed++;
            }

            if (!slice.hasNext()) break;
            page++;
        }
        log.info("[Refund] {} ~ {} processed={}, pages={}", from, to, processed, page + 1);
    }


}