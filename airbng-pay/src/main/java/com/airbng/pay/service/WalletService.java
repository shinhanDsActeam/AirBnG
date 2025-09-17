package com.airbng.pay.service;

import com.airbng.pay.dto.WalletBalanceResponse;
import com.airbng.pay.dto.WalletOverviewResponse;
import com.airbng.pay.dto.WalletTopupRequest;
import com.airbng.pay.dto.WalletWithdrawRequest;
import com.airbng.platform.security.principal.AirbngPrincipal;

public interface WalletService {
    WalletBalanceResponse getBalance(AirbngPrincipal principal);

    WalletOverviewResponse getOverview(AirbngPrincipal principal);

    void topup(AirbngPrincipal principal, String idemPotencyKey, WalletTopupRequest req);

    void withdraw(AirbngPrincipal principal, String idemPotencyKey, WalletWithdrawRequest req);
}
