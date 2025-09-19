package com.airbng.pay.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class WalletTopupRequest {
    @NotNull
    private Long accountId; //사용할 계좌 id
    @NotNull @Min(1)
    private BigDecimal balance; //충전 금액
}
