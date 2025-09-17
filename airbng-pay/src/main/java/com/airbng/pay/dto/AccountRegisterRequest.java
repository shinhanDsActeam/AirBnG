package com.airbng.pay.dto;

import lombok.Getter;

@Getter
public class AccountRegisterRequest {
    private Integer bankCode;
    private String accountNumber;
    private String holderName;
}
