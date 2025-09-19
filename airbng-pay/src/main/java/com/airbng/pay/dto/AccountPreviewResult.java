package com.airbng.pay.dto;

import com.airbng.common.base.BaseStatus;
import com.airbng.pay.domain.Account;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Builder
public class AccountPreviewResult {
    private Long accountId;
    private List<BankCodeResult> bankCodes;
    private String accountNumber;
    private BigDecimal  balance;
    private String holderName;
    private boolean isPrimary;
    private BaseStatus baseStatus;

    public static AccountPreviewResult from(Account account) {
        return AccountPreviewResult.builder()
                .accountId(account.getAccountId())
                .bankCodes(List.of(BankCodeResult.from(account.getBankInfo())))
                .accountNumber(account.getAccountNumber())
                .balance(account.getBalance())
                .holderName(account.getHolderName())
                .isPrimary(account.getIsPrimary())
                .baseStatus(account.getBaseStatus())
                .build();
    }
}
