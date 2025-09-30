package com.airbng.pay.service;

import com.airbng.pay.domain.Refund;
import com.airbng.pay.domain.WalletTxType;
import com.airbng.pay.domain.view.CompletedReservationView;
import com.airbng.pay.dto.*;
import com.airbng.platform.security.principal.AirbngPrincipal;

public interface WalletService {
    WalletBalanceResponse getBalance(AirbngPrincipal principal);

    WalletOverviewResponse getOverview(AirbngPrincipal principal);

    void topup(AirbngPrincipal principal, String idempotencyKey, WalletTopupRequest req);

    void withdraw(AirbngPrincipal principal, String idempotencyKey, WalletWithdrawRequest req);

    WalletHistoryResponse getHistory(AirbngPrincipal principal, Long cursor, WalletTxType type);

    void performRefund(Refund refund);

    void performSettlement(CompletedReservationView v);
}
