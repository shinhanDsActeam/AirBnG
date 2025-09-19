package com.airbng.pay.dto;

import com.airbng.pay.domain.WalletTx;
import com.airbng.pay.domain.WalletTxRole;
import com.airbng.pay.domain.WalletTxType;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
public class WalletTxHistoryResponse {
    private Long walletTxId;
    private WalletTxType txType;
    private BigDecimal amount;

    @JsonFormat(shape =  JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    public static WalletTxHistoryResponse from(WalletTx tx) {
        return WalletTxHistoryResponse.builder()
                .walletTxId(tx.getWalletTxId())
                .txType(tx.getWalletTxType())
                .amount(tx.getAmount())
                .createdAt(tx.getCreatedAt())
                .build();
    }
}
