package com.airbng.api.pay.dto.command;

public enum RefundMode {
    AUTO_FULL,        // 승인 전: 전액환불
    REVIEW_REQUIRED   // 승인 후: 정책 수수료 적용
}