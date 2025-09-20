package com.airbng.common;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

// 공통 비즈니스 ID 정의
@Component
public class BusinessIds {
    public static long ADMIN_MEMBER_ID;
    public static long SYSTEM_WALLET_ID;

    @Value("${business.ids.admin-member-id}")
    private long adminMemberId;

    @Value("${business.ids.system-wallet-id}")
    private long systemWalletId;

    @PostConstruct
    public void init() {
        ADMIN_MEMBER_ID = adminMemberId;
        SYSTEM_WALLET_ID = systemWalletId;
    }
}
