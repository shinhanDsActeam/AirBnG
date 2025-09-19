package com.airbng.pay.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class WalletWithdrawRequest {
    @NotNull
    long accountId;
}
