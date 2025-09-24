package com.airbng.api.pay.dto.command;

public enum RefundType {
    FULL,        // 승인 전: 전액환불
    PARTIAL   // 승인 후: 정책 수수료 적용
}