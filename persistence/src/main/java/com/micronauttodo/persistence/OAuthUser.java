package com.micronauttodo.persistence;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.NonNull;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Introspected
public class OAuthUser {

    @NonNull
    @NotBlank
    private final String iss;

    @NonNull
    @NotBlank
    private final String sub;

    @NonNull
    @NotBlank
    @Email
    private final String email;

    public OAuthUser(@NonNull String iss,
                      @NonNull String sub,
                      @NonNull String email) {
        this.iss = iss;
        this.sub = sub;
        this.email = email;
    }

    @NonNull
    public String getIss() {
        return iss;
    }

    @NonNull
    public String getSub() {
        return sub;
    }

    @NonNull
    public String getEmail() {
        return email;
    }
}
