package com.airbng.pay.controller;

import com.airbng.pay.dto.MyAccountsResponse;
import com.airbng.pay.dto.AccountRegisterRequest;
import com.airbng.pay.dto.BankCodeResult;
import com.airbng.pay.exception.BankInfoException;
import com.airbng.pay.service.AccountService;
import com.airbng.platform.common.response.BaseResponse;
import com.airbng.platform.security.principal.AirbngPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static com.airbng.platform.common.response.status.BaseResponseStatus.UNSUPPORTED_BANK;

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
    public BaseResponse<MyAccountsResponse> getMyAccounts(@AuthenticationPrincipal AirbngPrincipal principal) {
        return new BaseResponse<>(accountService.getMyAccounts(principal));
    }

    @GetMapping("/banks")
    @PreAuthorize("hasAnyAuthority('USER')")
    public BaseResponse<BankCodeResult> findByBankCode(Integer bankCode) {
        BankCodeResult result = accountService.findByBankCode(bankCode)
                .orElseThrow(() -> new BankInfoException(UNSUPPORTED_BANK));

        return new BaseResponse<>(result);
    }
}
