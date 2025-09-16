package com.airbng.pay.service;

import com.airbng.pay.dto.WalletBalanceResponse;
import com.airbng.platform.security.principal.AirbngPrincipal;

public interface WalletService {
    WalletBalanceResponse getBalance(AirbngPrincipal principal);
}
