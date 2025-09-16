package com.airbng.pay.domain;

public enum WalletTxRole {
    CREDIT, // 입금 balance_available 증가 or balance_reserved 증가
    DEBIT,  // 출금 balance_available 감소 or balance_reserved 감소
    MOVE    // 같은 지갑 내에서 보류 잔액 -> 보유 잔액
}
