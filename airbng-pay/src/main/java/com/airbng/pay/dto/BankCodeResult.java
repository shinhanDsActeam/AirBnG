package com.airbng.pay.dto;

import com.airbng.pay.domain.BankInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class BankCodeResult {
    private Integer bankCode;
    private String korName;
    private String engName;

    public static BankCodeResult from(BankInfo bankInfo) {
        return BankCodeResult.builder()
                .bankCode(bankInfo.getBankCode())
                .korName(bankInfo.getKorCode())
                .engName(bankInfo.getEngCode())
                .build();
    }
}
