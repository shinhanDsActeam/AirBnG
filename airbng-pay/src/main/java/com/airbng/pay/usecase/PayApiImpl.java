package com.airbng.pay.usecase;

import com.airbng.api.pay.PayApi;
import com.airbng.api.pay.dto.command.MakePaymentRequest;
import com.airbng.api.pay.dto.command.WalletCreateCommand;
import com.airbng.pay.domain.*;
import com.airbng.pay.exception.PaymentException;
import com.airbng.pay.exception.WalletException;
import com.airbng.pay.repository.PaymentRepository;
import com.airbng.pay.repository.WalletRepository;
import com.airbng.pay.util.UUIDUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.airbng.platform.common.response.status.BaseResponseStatus.*;

import static com.airbng.common.base.BaseStatus.ACTIVE;

@Service
@Slf4j
@RequiredArgsConstructor
class PayApiImpl implements PayApi {
    private final PaymentRepository paymentRepository;
    private final WalletRepository walletRepository;

    @Transactional
    @Override
    public void createWallet(WalletCreateCommand cmd) {
        long memberId = cmd.getMemberId();
        Wallet wallet = Wallet.builder()
                .memberId(memberId)
                .balanceAvailable(BigDecimal.ZERO)
                .balanceReserved(BigDecimal.ZERO)
                .status(ACTIVE)
                .build();
        walletRepository.save(wallet);
    }

    @Override
    @Transactional
    public Long pay(MakePaymentRequest request) {
        UUID payIdemKey = UUIDUtil.fromString(request.getIdemKeyRaw());
        PayMethod method = PayMethod.valueOf(request.getMethod());

        // 멱등키 존재시 기존 결제ID 반환
        Payment existed = paymentRepository.findByPayIdemKey(payIdemKey).orElse(null);
        if (existed != null) {
            log.info("멱등키 존재 - {}. paymentId: {}", payIdemKey, existed.getPaymentId());
            return existed.getPaymentId();
        }

        if(method == PayMethod.WALLET) {
            return payByWallet(request, payIdemKey);
        }else if(method == PayMethod.PG) {
            return payByPG(request, payIdemKey);
        }

        throw new PaymentException(FAILED_PAYMENT);
    }

    /**
     * 짐페이머니에 대한 결제 처리
     * @param request
     * @param payIdemKey
     * @return
     */
    private Long payByWallet(MakePaymentRequest request, UUID payIdemKey) {

        // 결제 금액, 수수료, 총액
        BigDecimal amount = BigDecimal.valueOf(request.getAmount());
        BigDecimal fee = BigDecimal.valueOf(request.getFee());
        BigDecimal totalAmount = amount.add(fee);

        // 락 대상 지갑 목록 구성
        List<Long> memberIds = new ArrayList<>(List.of(request.getPayerId(), request.getPayeeId()));
        if (fee.compareTo(BigDecimal.ZERO) > 0) {
            memberIds.add(ADMIN_MEMBER_ID);
        }

        // 대상 지갑 락
        List<Wallet> wallets = walletRepository.findWalletsWithLockByMemberIds(memberIds);

        Wallet payerWallet = walletPickByMemberIdOrThrow(wallets, request.getPayerId());
        Wallet payeeWallet = walletPickByMemberIdOrThrow(wallets, request.getPayeeId());
    }

    private Wallet walletPickByMemberIdOrThrow(List<Wallet> wallets, Long memberID) {
        return wallets.stream()
                .filter(w -> w.getMemberId().equals(memberID))
                .findFirst()
                .orElseThrow(()-> new WalletException(INVALID_WALLET));
    }


    private Long payByPG(MakePaymentRequest request, UUID payIdemKey) {
        // TODO : PayMethod.PG 결제 처리 - 외부 PG 연동 필요
        throw new PaymentException(UNSUPPORTED_PAY_METHOD);
    }

}
