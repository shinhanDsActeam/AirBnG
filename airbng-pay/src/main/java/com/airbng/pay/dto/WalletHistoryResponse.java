package com.airbng.pay.dto;

import com.airbng.pay.domain.Wallet;
import com.airbng.pay.domain.WalletTx;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Builder
public class WalletHistoryResponse {
    private BigDecimal balance; //보유 포인트
    private List<WalletTxHistoryResponse> items;
    private Long nextCursor;
    private boolean hasNext;

    public static WalletHistoryResponse from(Wallet wallet, List<WalletTx> lists, Long nextCursor, boolean hasNext) {
        List<WalletTxHistoryResponse> history = lists.stream()
                .map(WalletTxHistoryResponse::from)
                .toList();

        return WalletHistoryResponse.builder()
                .balance(wallet.getBalanceAvailable())
                .items(history)
                .nextCursor(nextCursor)
                .hasNext(hasNext)
                .build();
    }
}
