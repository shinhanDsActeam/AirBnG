package com.airbng.pay.controller;

import com.airbng.pay.domain.WalletTxType;
import com.airbng.pay.dto.*;
import com.airbng.pay.service.WalletService;
import com.airbng.platform.common.response.BaseResponse;
import com.airbng.platform.security.principal.AirbngPrincipal;
import jakarta.validation.Valid;
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
     * /me/balance : 페이머니만 조회하는 2-1 페이지
     * /me/overview : 페이머니 + 등록계좌 조회 2-2 페이지
     * /me/topup : 페이머니 충전 2-3 페이지
     * /me/withdraw : 페이머니 출금 2-4 페이지
     * /me/history : 페이머니 거래내역 조회 2-5 페이지
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
                                                   @RequestHeader("Idempotency-Key") String idempotencyKey,
                                                   @RequestBody @Valid WalletTopupRequest req) {
        walletService.topup(principal, idempotencyKey, req);
        return new BaseResponse<>(SUCCESS);
    }

    @PostMapping("/me/withdraw")
    @PreAuthorize("hasAnyAuthority('USER')")
    public BaseResponse<String> withdraw(@AuthenticationPrincipal AirbngPrincipal principal,
                                         @RequestHeader("Idempotency-Key") String idempotencyKey,
                                         @RequestBody @Valid WalletWithdrawRequest req) {
        walletService.withdraw(principal, idempotencyKey, req);
        return new BaseResponse<>(SUCCESS);
    }

    @GetMapping("/me/history")
    @PreAuthorize("hasAnyAuthority('USER')")
    public BaseResponse<WalletHistoryResponse> getHistory(@AuthenticationPrincipal AirbngPrincipal principal,
                                                          @RequestParam(required = false) Long cursor,
                                                          @RequestParam(required = false)WalletTxType type) {
        return new BaseResponse<>(walletService.getHistory(principal, cursor, type));
    }
}
