package com.airbng.api.pay;

import com.airbng.api.pay.dto.command.WalletCreateCommand;

public interface PayApi {
    void createWallet(WalletCreateCommand cmd);
}
