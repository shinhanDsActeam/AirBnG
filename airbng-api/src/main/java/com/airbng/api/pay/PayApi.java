package com.airbng.api.pay;

import com.airbng.api.pay.dto.command.*;

public interface PayApi {
    void createWallet(WalletCreateCommand cmd);
    Long pay(MakePaymentRequest request); // payment Id 반환
}
