package com.airbng.pay.service;

import com.airbng.pay.domain.*;
import com.airbng.pay.dto.*;
import com.airbng.pay.exception.AccountException;
import com.airbng.pay.exception.WalletException;
import com.airbng.pay.repository.AccountRepository;
import com.airbng.pay.repository.WalletRepository;
import com.airbng.pay.repository.WalletTxRepository;
import com.airbng.platform.security.principal.AirbngPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.airbng.platform.common.response.status.BaseResponseStatus.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class WalletServiceImpl implements WalletService {

    private final WalletRepository walletRepository;
    private final WalletTxRepository walletTxRepository;
    private final AccountRepository accountRepository;
    private static final int PAGE_SIZE = 10;
    private static final BigDecimal MIN_TOPUP_AMOUNT = new BigDecimal("1000");

    @Override
    public WalletBalanceResponse getBalance(AirbngPrincipal principal) {
        Wallet wallet = walletRepository.findByMemberId(principal.getId())
                .orElseThrow(() -> new WalletException(INVALID_WALLET));
        return WalletBalanceResponse.from(wallet);
    }

    @Transactional(readOnly = true)
    @Override
    public WalletOverviewResponse getOverview(AirbngPrincipal principal) {
        Wallet wallet = walletRepository.findByMemberId(principal.getId())
                .orElseThrow(() -> new WalletException(INVALID_WALLET));
        List<Account> accounts =
                accountRepository.findAllByWalletWalletIdOrderByIsPrimaryDescAccountIdAsc(wallet.getWalletId());
        return WalletOverviewResponse.from(wallet, accounts);
    }

    @Transactional
    @Override
    public void topup(AirbngPrincipal principal, String idemKeyRaw, WalletTopupRequest req) {
        UUID idemKey = UUID.fromString(idemKeyRaw);

        Optional<WalletTx> existing = walletTxRepository.findByWalletIdemKey(idemKey);
        if (existing.isPresent()) {
            log.info("[페이머니 충전] 이미 진행된 결과");
            throw new WalletException(ALREADY_PROCESSED);
        }
        Long memberId = principal.getId();
        Wallet wallet = walletRepository.findByMemberIdForUpdate(memberId)
                .orElseThrow(() -> new WalletException(INVALID_WALLET));

        Account account = accountRepository.findForUpdate(req.getAccountId(), wallet.getWalletId())
                .orElseThrow(() -> new AccountException(WALLET_ACCOUNT_MISMATCH));

        BigDecimal balance = req.getBalance();
        if (balance.compareTo(MIN_TOPUP_AMOUNT) < 0) {
            throw new WalletException(INSUFFICIENT_TOPUP);
        }

        if (account.getBalance().compareTo(balance) < 0) {
            throw new AccountException(INSUFFICIENT_BALANCE_ACCOUNT);
        }

        account.updateBalance(balance.negate());
        wallet.addBalanceAvailable(balance);

        WalletTx tx = WalletTx.builder()
                .wallet(wallet)
                .payment(null)
                .walletTxType(WalletTxType.TOPUP)
                .walletTxRole(WalletTxRole.CREDIT)
                .amount(balance)
                .walletIdemKey(idemKey)
                .build();
        walletTxRepository.save(tx);
    }

    @Transactional
    @Override
    public void withdraw(AirbngPrincipal principal, String idemKeyRaw, WalletWithdrawRequest req) {
        UUID idemKey = UUID.fromString(idemKeyRaw);
        Optional<WalletTx> existing = walletTxRepository.findByWalletIdemKey(idemKey);
        if (existing.isPresent()) {
            log.info("[페이머니 출금] 이미 진행된 결과");
            throw new WalletException(ALREADY_PROCESSED);
        }

        Long memberId = principal.getId();
        Wallet wallet = walletRepository.findByMemberIdForUpdate(memberId)
                .orElseThrow(() -> new WalletException(INVALID_WALLET));

        BigDecimal balance = wallet.getBalanceAvailable();
        Account account = accountRepository.findForUpdate(req.getAccountId(), wallet.getWalletId())
                .orElseThrow(() -> new AccountException(WALLET_ACCOUNT_MISMATCH));

        if (balance.signum() <= 0) {
            throw new WalletException(INSUFFICIENT_BALANCE);
        }
        

        wallet.subtractBalanceAvailable(balance);
        account.updateBalance(balance);

        WalletTx tx = WalletTx.builder()
                .wallet(wallet)
                .payment(null)
                .walletTxType(WalletTxType.WITHDRAW)
                .walletTxRole(WalletTxRole.DEBIT)
                .amount(balance)
                .walletIdemKey(idemKey)
                .build();
        walletTxRepository.save(tx);
    }

    @Transactional(readOnly = true)
    @Override
    public WalletHistoryResponse getHistory(AirbngPrincipal principal, Long cursor, WalletTxType type) {
        Wallet wallet = walletRepository.findByMemberId(principal.getId())
                .orElseThrow(() -> new WalletException(INVALID_WALLET));

        List<WalletTx> fetched = walletTxRepository.findSliceByWalletIdAndCursorDesc(
                wallet.getWalletId(),
                cursor,
                type,
                PageRequest.of(0, PAGE_SIZE+1));

        boolean hasNext = fetched.size() > PAGE_SIZE;
        if (hasNext) {
            fetched = fetched.subList(0, PAGE_SIZE);
        }
        Long nextCursor = hasNext ? fetched.get(fetched.size() -1).getWalletTxId() : null;

        return WalletHistoryResponse.from(wallet, fetched, nextCursor, hasNext);
    }
}
