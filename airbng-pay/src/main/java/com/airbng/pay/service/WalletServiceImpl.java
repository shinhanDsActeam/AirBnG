package com.airbng.pay.service;

import com.airbng.pay.domain.Account;
import com.airbng.pay.domain.Wallet;
import com.airbng.pay.dto.WalletBalanceResponse;
import com.airbng.pay.dto.WalletOverviewResponse;
import com.airbng.pay.repository.AccountRepository;
import com.airbng.pay.repository.WalletRepository;
import com.airbng.platform.security.principal.AirbngPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService {

    private final WalletRepository walletRepository;
    private final AccountRepository accountRepository;

    @Override
    public WalletBalanceResponse getBalance(AirbngPrincipal principal) {
        Wallet wallet = walletRepository.findByMemberId(principal.getId());
        return WalletBalanceResponse.from(wallet);
    }

    @Transactional(readOnly = true)
    @Override
    public WalletOverviewResponse getOverview(AirbngPrincipal principal) {
        Wallet wallet = walletRepository.findByMemberId(principal.getId());
        List<Account> accounts =
                accountRepository.findAllByWalletWalletIdOrderByIsPrimaryDescAccountIdAsc(wallet.getWalletId());
        return WalletOverviewResponse.from(wallet, accounts);
    }
}
