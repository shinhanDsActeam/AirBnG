package com.airbng.pay.service;

import com.airbng.pay.dto.MyAccountsResponse;
import com.airbng.pay.dto.AccountRegisterRequest;
import com.airbng.pay.dto.BankCodeResult;
import com.airbng.platform.security.principal.AirbngPrincipal;

import java.util.Optional;

public interface AccountService {
    void register(AccountRegisterRequest req, AirbngPrincipal user);
    MyAccountsResponse getMyAccounts(AirbngPrincipal principal);
    Optional<BankCodeResult> findByBankCode(Integer bankCode);

    void delete(Long accountId, AirbngPrincipal principal);

    void setPrimaryAccount(Long accountId, AirbngPrincipal principal);
}
