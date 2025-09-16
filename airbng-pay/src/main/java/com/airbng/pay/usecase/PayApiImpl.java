package com.airbng.pay.usecase;

import com.airbng.api.pay.PayApi;
import com.airbng.api.pay.dto.command.WalletCreateCommand;
import com.airbng.pay.domain.Wallet;
import com.airbng.pay.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
class PayApiImpl implements PayApi {

    private final WalletRepository walletRepository;

    @Transactional
    @Override
    public void createWallet(WalletCreateCommand cmd) {
        long memberId = cmd.getMemberId();
        Wallet wallet = Wallet.builder()
                .memberId(memberId)
                .balanceAvailable(BigDecimal.ZERO)
                .balanceReserved(BigDecimal.ZERO)
                .build();
        walletRepository.save(wallet);
    }
}
