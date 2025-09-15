package com.airbng.pay.domain;

import com.airbng.common.base.BaseTime;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WalletTx extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long walletTxId;

    @ManyToOne(fetch = FetchType.LAZY)
    private Wallet wallet;

    @ManyToOne(fetch = FetchType.LAZY)
    private Payment payment;

    @Column(nullable = false)
    private WalletTxType walletTxType;

    @Column(nullable = false)
    private WalletTxRole walletTxRole;

    @Column(nullable = false)
    private String walletItemKey;

    @Column(nullable = false)
    private BigDecimal amount; // 사용 금액
}
