package com.airbng.pay.usecase;

import com.airbng.api.pay.WalletApi;
import com.airbng.api.pay.dto.view.WalletInfoView;
import com.airbng.pay.domain.Wallet;
import com.airbng.pay.exception.WalletException;
import com.airbng.pay.repository.WalletRepository;
import com.airbng.platform.common.response.status.BaseResponseStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
class WalletApiImpl implements WalletApi {

    private final WalletRepository walletRepository;

    @Override
    public WalletInfoView getWalletInfo(Long memberId) {
        Wallet wallet = walletRepository.findByMemberId(memberId)
                .orElseThrow(() -> new WalletException(BaseResponseStatus.INVALID_WALLET));

        return WalletInfoView.builder()
                .walletId(wallet.getWalletId())
                .balance(wallet.getBalanceAvailable()).build();
    }
}
