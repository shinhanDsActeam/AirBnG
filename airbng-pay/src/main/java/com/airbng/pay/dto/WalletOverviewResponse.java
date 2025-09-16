package com.airbng.pay.dto;

import com.airbng.pay.domain.Account;
import com.airbng.pay.domain.Wallet;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Builder
public class WalletOverviewResponse {
    private Long walletId;
    private BigDecimal balance; //사용 가능 금액
    private List<AccountPreviewResult> accounts;

    public static WalletOverviewResponse from(Wallet wallet, List<Account> accounts) {
        return WalletOverviewResponse.builder()
                .walletId(wallet.getWalletId())
                .balance(wallet.getBalanceAvailable())
                .accounts(accounts.stream()
                        .map(AccountPreviewResult::from)
                        .toList())
                .build();
    }
}
