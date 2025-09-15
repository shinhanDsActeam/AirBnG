package com.airbng.api.pay.dto.command;

import lombok.Getter;

@Getter
public class WalletCreateCommand {
    private final Long memberId;

    public WalletCreateCommand(Long memberId) {
        this.memberId = memberId;
    }
}
