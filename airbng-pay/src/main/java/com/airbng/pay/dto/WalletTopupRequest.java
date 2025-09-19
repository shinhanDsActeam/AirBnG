package com.airbng.pay.dto;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class WalletTopupRequest {
    private Long accountId; //사용할 계좌 id
    private BigDecimal balance; //충전 금액
}
