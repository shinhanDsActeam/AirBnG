package com.airbng.pay.dto;

import com.airbng.pay.domain.Account;
import com.airbng.pay.domain.Wallet;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class AccountCheckResponse {
    private long walletId;
    private List<AccountPreviewResult> accounts;

    public static AccountCheckResponse from(Wallet wallet, List<Account> accounts) {
        List<AccountPreviewResult> results = accounts.stream()
                .map(AccountPreviewResult::from)
                .toList();

        return AccountCheckResponse.builder()
                .walletId(wallet.getWalletId())
                .accounts(results)
                .build();
    }
}

