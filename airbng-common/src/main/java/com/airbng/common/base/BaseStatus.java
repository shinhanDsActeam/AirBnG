package com.airbng.common.base;

public enum BaseStatus {

        ACTIVE,
        DELETE;

        public boolean isActive() {
                return this == ACTIVE;
        }

}
