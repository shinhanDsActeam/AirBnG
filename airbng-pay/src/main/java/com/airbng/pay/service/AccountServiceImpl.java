package com.airbng.pay.service;

import com.airbng.pay.domain.Account;
import com.airbng.pay.domain.BankInfo;
import com.airbng.pay.domain.Wallet;
import com.airbng.pay.dto.AccountRegisterRequest;
import com.airbng.pay.dto.BankCodeResult;
import com.airbng.pay.dto.MyAccountsResponse;
import com.airbng.pay.exception.AccountException;
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
import java.util.List;
import java.util.Optional;

import static com.airbng.common.base.BaseStatus.ACTIVE;
import static com.airbng.platform.common.response.status.BaseResponseStatus.*;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final WalletRepository walletRepository;
    private final BankInfoRepository bankInfoRepository;
    private final AccountValidationService accountValidationService;

    @Transactional
    @Override
    public void register(AccountRegisterRequest req, AirbngPrincipal user) {
        long memberId = user.getId();
        Wallet wallet = walletRepository.findByMemberId(memberId)
                .orElseThrow(() -> new WalletException(INVALID_WALLET));
        if (!bankInfoRepository.existsByBankCode(req.getBankCode())) throw new BankInfoException(UNSUPPORTED_BANK);
        BankInfo bankInfo = bankInfoRepository.findByBankCode((req.getBankCode()));

        boolean isPrimary = false;
        if (!accountRepository.existsByWallet(wallet)) {
            isPrimary = true;
        }

        String accountNumber = req.getAccountNumber().replace("-","");
        if (!accountValidationService.isValidAccountNumber(accountNumber)) {
            throw new BankInfoException(INVALID_ACCOUNT);
        }
        if (accountRepository.existsByWalletWalletIdAndAccountNumber(wallet.getWalletId(), accountNumber))
            throw new WalletException(DUPLICATE_ACCOUNT);

        Account account = Account.builder()
                .wallet(wallet)
                .bankInfo(bankInfo)
                .accountNumber(accountNumber)
                .holderName(req.getHolderName())
                .isPrimary(isPrimary)
                .balance(new BigDecimal(1000000))
                .build();

        accountRepository.save(account);

    }

    @Transactional(readOnly = true)
    @Override
    public MyAccountsResponse getMyAccounts(AirbngPrincipal principal) {
        long memberId = principal.getId();
        Wallet wallet = walletRepository.findByMemberId(memberId)
                .orElseThrow(() -> new WalletException(INVALID_WALLET));
        List<Account> accounts =
                accountRepository.findAllByWalletWalletIdOrderByIsPrimaryDescAccountIdAsc(wallet.getWalletId());
        return MyAccountsResponse.from(wallet, accounts);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<BankCodeResult> findByBankCode(Integer bankCode) {
        if (!bankInfoRepository.existsByBankCode(bankCode))
            throw new BankInfoException(UNSUPPORTED_BANK);
        BankInfo bankInfo = bankInfoRepository.findByBankCode(bankCode);
        return Optional.of(BankCodeResult.from(bankInfo));
    }

    @Transactional
    @Override
    public void delete(Long accountId, AirbngPrincipal principal) {
        long memberId = principal.getId();
        Wallet wallet = walletRepository.findByMemberId(memberId)
                .orElseThrow(() -> new WalletException(INVALID_WALLET));

        Account account = accountRepository.findForUpdate(accountId, wallet.getWalletId())
                .orElseThrow(() -> new WalletException(WALLET_ACCOUNT_MISMATCH));

        accountRepository.delete(account);
    }

    @Transactional
    @Override
    public void setPrimaryAccount(Long accountId, AirbngPrincipal principal) {
        long memberId = principal.getId();
        Wallet wallet = walletRepository.findByMemberId(memberId)
                .orElseThrow(() -> new WalletException(INVALID_WALLET));

        boolean flag = accountRepository.existsByWalletWalletIdAndAccountId(wallet.getWalletId(), accountId);
        if(!flag) throw new WalletException(WALLET_ACCOUNT_MISMATCH);

        int row = accountRepository.updatePrimaryAccount(wallet.getWalletId(), accountId);
        if (row == 0) throw new AccountException(FAILED_UPDATE_PRIMARY);
    }
}
