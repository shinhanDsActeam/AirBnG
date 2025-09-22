package com.airbng.api.pay;

import com.airbng.api.pay.dto.command.WalletCreateCommand;
import com.airbng.api.pay.dto.view.WalletInfoView;

public interface WalletApi {
    WalletInfoView getWalletInfo(Long memberId);
}
