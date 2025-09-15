package com.airbng.pay.domain;

import com.airbng.common.base.BaseTime;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Entity
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {"memberId"})
})
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Wallet extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long walletId;

    @Column(nullable = false)
    private Long memberId;

    @Column(nullable = false)
    private BigDecimal balanceAvailable; // 보유 잔액

    @Column(nullable = false)
    private BigDecimal balanceReserved;  // 보류 잔액

    @OneToMany(mappedBy = "wallet", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Account> accounts;

    public void addBalanceAvailable(BigDecimal amount) {
        // TODO : 예외처리
        this.balanceAvailable = this.balanceAvailable.add(amount);
    }

    public void subtractBalanceAvailable(BigDecimal amount) {
        // TODO : 예외처리
        this.balanceAvailable = this.balanceAvailable.subtract(amount);
    }



}
