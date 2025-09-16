package com.airbng.pay.controller;

import com.airbng.pay.dto.WalletBalanceResponse;
import com.airbng.pay.service.WalletService;
import com.airbng.platform.common.response.BaseResponse;
import com.airbng.platform.security.principal.AirbngPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/wallet")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

    @GetMapping("/me/balance")
    @PreAuthorize("hasAnyAuthority('USER')")
    public BaseResponse<WalletBalanceResponse> getBalance(@AuthenticationPrincipal AirbngPrincipal principal) {
        return new BaseResponse<>(walletService.getBalance(principal));
    }
}
