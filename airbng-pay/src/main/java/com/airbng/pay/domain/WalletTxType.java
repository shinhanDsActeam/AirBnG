package com.airbng.pay.domain;

public enum WalletTxType {
    TOPUP,     // 충전
    REFUND,    // 환불 (keeper reserved → dropper available)
    PAYMENT,   // 결제 (dropper available → keeper reserved)
    WITHDRAW,  // 출금
    SETTLEMENT // 정산 (예약 완료 시, keeper reserved → keeper available)
}
