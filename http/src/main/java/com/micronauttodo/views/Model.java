package com.micronauttodo.views;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.security.authentication.Authentication;

@Introspected
public class Model {

    @Nullable
    private Authentication authentication;

    @Nullable
    public Authentication getAuthentication() {
        return authentication;
    }

    public void setAuthentication(@Nullable Authentication authentication) {
        this.authentication = authentication;
    }
}
