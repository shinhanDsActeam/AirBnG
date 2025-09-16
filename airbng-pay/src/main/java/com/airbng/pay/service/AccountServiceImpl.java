package com.airbng.pay.service;

import com.airbng.pay.domain.Account;
import com.airbng.pay.domain.BankInfo;
import com.airbng.pay.domain.Wallet;
import com.airbng.pay.dto.AccountRegisterRequest;
import com.airbng.pay.exception.BankInfoException;
import com.airbng.pay.exception.WalletException;
import com.airbng.pay.repository.AccountRepository;
import com.airbng.pay.repository.BankInfoRepository;
import com.airbng.pay.repository.WalletRepository;
import com.airbng.platform.security.principal.AirbngPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

import static com.airbng.common.base.Available.YES;
import static com.airbng.platform.common.response.status.BaseResponseStatus.*;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final WalletRepository walletRepository;
    private final BankInfoRepository bankInfoRepository;

    @Transactional
    @Override
    public void register(AccountRegisterRequest req, AirbngPrincipal user) {
        long memberId = user.getId();
        if (!walletRepository.existsByMemberId(memberId)) throw new WalletException(INVALID_WALLET);
        Wallet wallet = walletRepository.findByMemberId(memberId);
        if (!bankInfoRepository.existsByBankCode(req.getBankCode())) throw new BankInfoException(UNSUPPORTED_BANK);
        BankInfo bankInfo = bankInfoRepository.findByBankCode((req.getBankCode()));
        if (accountRepository.existsByWalletWalletIdAndAccountNumber(wallet.getWalletId(), req.getAccountNumber()))
            throw new WalletException(DUPLICATE_ACCOUNT);

        boolean isPrimary = false;
        if (!accountRepository.existsByWallet(wallet)) {
            isPrimary = true;
        }

        Account account = Account.builder()
                .wallet(wallet)
                .bankInfo(bankInfo)
                .accountNumber(req.getAccountNumber())
                .holderName(req.getHolderName())
                .isPrimary(isPrimary)
                .status(YES)
                .balance(BigDecimal.ZERO)
                .build();

        accountRepository.save(account);

    }
}
