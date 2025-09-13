package com.airbng.common.base;

// Locker 운영 여부
public enum Available {
    YES,
    NO;

    public boolean isAvailable() {
        return this == YES;
    }
}
