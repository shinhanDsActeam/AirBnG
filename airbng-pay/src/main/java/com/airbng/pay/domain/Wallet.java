package com.airbng.pay.domain;

import com.airbng.common.base.BaseStatus;
import com.airbng.common.base.BaseTime;
import com.airbng.pay.exception.WalletException;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

import static com.airbng.platform.common.response.status.BaseResponseStatus.INSUFFICIENT_BALANCE;

@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Wallet extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long walletId;

    @Column(nullable = false, unique = true)
    private Long memberId;

    @Column(nullable = false)
    private BigDecimal balanceAvailable; // 보유 잔액

    @Column(nullable = false)
    private BigDecimal balanceReserved;  // 보류 잔액

    @OneToMany(mappedBy = "wallet", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Account> accounts;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BaseStatus status;

    public void addBalanceAvailable(BigDecimal amount) {
        this.balanceAvailable = this.balanceAvailable.add(amount);
    }

    public void subtractBalanceAvailable(BigDecimal amount) {
        if(this.balanceAvailable.compareTo(amount) < 0) {
            throw new WalletException(INSUFFICIENT_BALANCE);
        }

        this.balanceAvailable = this.balanceAvailable.subtract(amount);
    }

    public void addBalanceReserved(BigDecimal amount) {
        this.balanceReserved = this.balanceReserved.add(amount);
    }

    public void subtractBalanceReserved(BigDecimal amount) {
        if(this.balanceReserved.compareTo(amount) < 0) {
            throw new WalletException(INSUFFICIENT_BALANCE);
        }
        this.balanceReserved = this.balanceReserved.subtract(amount);
    }

}
