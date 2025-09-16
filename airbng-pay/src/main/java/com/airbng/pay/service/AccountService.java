package com.airbng.pay.service;

import com.airbng.pay.dto.AccountCheckResponse;
import com.airbng.pay.dto.AccountRegisterRequest;
import com.airbng.platform.security.principal.AirbngPrincipal;

public interface AccountService {
    void register(AccountRegisterRequest req, AirbngPrincipal user);
    AccountCheckResponse checkAccount(AirbngPrincipal principal);
}
