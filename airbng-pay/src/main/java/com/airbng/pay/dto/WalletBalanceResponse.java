package com.airbng.pay.dto;


import com.airbng.pay.domain.Wallet;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Builder
@Getter
public class WalletBalanceResponse {
    private BigDecimal balance; //보유 잔액

    public static WalletBalanceResponse from (Wallet wallet) {
        return WalletBalanceResponse.builder()
                .balance(wallet.getBalanceAvailable())
                .build();
    }
}
