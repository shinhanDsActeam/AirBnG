package com.airbng.domain.base;

public enum BaseStatus {

        ACTIVE,
        DELETE;

        public boolean isActive() {
                return this == ACTIVE;
        }

}
