package com.airbng.pay.controller;

import com.airbng.pay.dto.AccountCheckResponse;
import com.airbng.pay.dto.AccountRegisterRequest;
import com.airbng.pay.service.AccountService;
import com.airbng.platform.common.response.BaseResponse;
import com.airbng.platform.security.principal.AirbngPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/account")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PostMapping
    @PreAuthorize("hasAnyAuthority('USER')")
    public BaseResponse<String> requestAccount(
            @RequestBody AccountRegisterRequest req,
            @AuthenticationPrincipal AirbngPrincipal principal) {
        accountService.register(req, principal);
        return new BaseResponse<>("계좌 생성 성공");
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('USER')")
    public BaseResponse<AccountCheckResponse> checkAccount(@AuthenticationPrincipal AirbngPrincipal principal) {
        return new BaseResponse<>(accountService.checkAccount(principal));
    }
}
