package com.airbng.api.pay.dto.view;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Builder
@Getter
public class WalletInfoView {
    Long walletId;
    BigDecimal balance;
}
