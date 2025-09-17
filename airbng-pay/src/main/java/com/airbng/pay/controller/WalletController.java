package com.airbng.pay.controller;

import com.airbng.pay.dto.WalletBalanceResponse;
import com.airbng.pay.dto.WalletOverviewResponse;
import com.airbng.pay.dto.WalletTopupRequest;
import com.airbng.pay.service.WalletService;
import com.airbng.platform.common.response.BaseResponse;
import com.airbng.platform.security.principal.AirbngPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static com.airbng.platform.common.response.status.BaseResponseStatus.SUCCESS;

@RestController
@RequestMapping("/wallet")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

    /**
     * 최종 프론트 연동후에는 지울 주석
     * /me/balance : 포인트만 조회하는 2-1 페이지
     * /me/overview : 포인트 + 등록계좌 조회 2-2 페이지
     */
    @GetMapping("/me/balance")
    @PreAuthorize("hasAnyAuthority('USER')")
    public BaseResponse<WalletBalanceResponse> getBalance(@AuthenticationPrincipal AirbngPrincipal principal) {
        return new BaseResponse<>(walletService.getBalance(principal));
    }

    @GetMapping("/me/overview")
    @PreAuthorize("hasAnyAuthority('USER')")
    public BaseResponse<WalletOverviewResponse> getOverview(@AuthenticationPrincipal AirbngPrincipal principal) {
        return new BaseResponse<>(walletService.getOverview(principal));
    }

    @PostMapping("/me/topup")
    @PreAuthorize("hasAnyAuthority('USER')")
    public BaseResponse<String> topup(@AuthenticationPrincipal AirbngPrincipal principal,
                                                   @RequestHeader("IdemPotency-Key") String idemPotencyKey,
                                                   @RequestBody WalletTopupRequest req) {
        walletService.topup(principal, idemPotencyKey, req);
        return new BaseResponse<>(SUCCESS);
    }
}
