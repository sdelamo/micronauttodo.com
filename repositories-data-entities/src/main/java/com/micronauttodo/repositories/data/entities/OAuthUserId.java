package com.micronauttodo.repositories.data.entities;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.data.annotation.Embeddable;
import javax.validation.constraints.NotBlank;

@Embeddable
public class OAuthUserId {
    @NonNull
    @NotBlank
    private final String issuer;

    @NotBlank
    @NonNull
    private final String sub;

    public OAuthUserId(@NonNull String issuer,
                       @NonNull String sub) {
        this.issuer = issuer;
        this.sub = sub;
    }

    @NonNull
    public String getIssuer() {
        return issuer;
    }

    @NonNull
    public String getSub() {
        return sub;
    }
}
