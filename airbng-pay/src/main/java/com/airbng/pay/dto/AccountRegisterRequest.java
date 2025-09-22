package com.airbng.pay.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class AccountRegisterRequest {
    @NotNull
    private Integer bankCode;
    @NotNull
    private String accountNumber;
    @NotNull
    private String holderName;
}
