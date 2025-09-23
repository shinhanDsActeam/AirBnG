package com.airbng.api.security;

import com.airbng.platform.security.principal.AirbngPrincipal;

public interface PrincipalApi {
    AirbngPrincipal loadById(Long memberId);

    default AirbngPrincipal requireById(Long memberId) {
        AirbngPrincipal p = loadById(memberId);
        if (p == null) throw new IllegalArgumentException("Principal not found: " + memberId);
        return p;
    }
}
