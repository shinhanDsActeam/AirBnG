package com.airbng.platform.util;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public final class UUIDUtil {
    private UUIDUtil() {
    }

    // 새 v4 UUID 생성
    public static UUID generate() {
        return UUID.randomUUID(); // 기본 v4
    }

    public static UUID generateFromString(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("UUID name is required");
        }
        return UUID.nameUUIDFromBytes(name.trim().getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 문자열을 UUID v4 객체로 변환 (형식/버전 틀리면 IllegalArgumentException)
     */
    public static UUID fromString(String raw) {
        if (raw == null || raw.isBlank()) {
            throw new IllegalArgumentException("idempotency key is required");
        }
        try {
            return UUID.fromString(raw.trim());
        } catch (Exception e) {
            throw new IllegalArgumentException("Idempotency-Key 형식 오류");
        }
    }

}