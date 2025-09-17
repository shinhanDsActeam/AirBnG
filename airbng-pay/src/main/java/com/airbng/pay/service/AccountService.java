package com.airbng.pay.service;

import com.airbng.pay.dto.AccountCheckResponse;
import com.airbng.pay.dto.AccountRegisterRequest;
import com.airbng.pay.dto.BankCodeResult;
import com.airbng.platform.security.principal.AirbngPrincipal;

import java.util.Optional;

public interface AccountService {
    void register(AccountRegisterRequest req, AirbngPrincipal user);
    AccountCheckResponse checkAccount(AirbngPrincipal principal);
    Optional<BankCodeResult> findByBankCode(Integer bankCode);
}
