package com.airbng.domain.base;

public enum Available {

    YES,
    NO;

    public boolean isAvailable() {
        return this == YES;
    }
}
