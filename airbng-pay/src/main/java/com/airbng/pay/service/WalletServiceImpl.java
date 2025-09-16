package com.airbng.pay.service;

import com.airbng.pay.domain.Wallet;
import com.airbng.pay.dto.WalletBalanceResponse;
import com.airbng.pay.repository.WalletRepository;
import com.airbng.platform.security.principal.AirbngPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService {

    private final WalletRepository walletRepository;

    @Override
    public WalletBalanceResponse getBalance(AirbngPrincipal principal) {
        Wallet wallet = walletRepository.findByMemberId(principal.getId());
        return WalletBalanceResponse.from(wallet);
    }
}
