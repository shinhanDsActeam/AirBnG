package com.airbng.pay.service;

import com.airbng.pay.dto.AccountRegisterRequest;
import com.airbng.platform.security.principal.AirbngPrincipal;
import org.springframework.security.core.userdetails.UserDetails;

public interface AccountService {
    void register(AccountRegisterRequest req, AirbngPrincipal user);
}
