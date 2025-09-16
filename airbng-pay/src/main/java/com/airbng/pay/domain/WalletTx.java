package com.airbng.pay.domain;

import com.airbng.common.base.BaseTime;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

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
    @JoinColumn(name = "wallet_id", nullable = false)
    private Wallet wallet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id")
    private Payment payment;

    @Column(nullable = false)
    private WalletTxType walletTxType;

    @Column(nullable = false)
    private WalletTxRole walletTxRole;

    @Column(columnDefinition = "BINARY(16)",nullable = false)
    private UUID walletIdemKey;

    @Column(nullable = false)
    private BigDecimal amount; // 사용 금액
}
