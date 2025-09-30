package com.airbng.pay.domain;

public enum MasterTxRole {
    CREDIT, // 입금
    DEBIT,  // 출금
    MOVE    // 같은 지갑 내에서 보류 잔액 -> 보유 잔액
}
