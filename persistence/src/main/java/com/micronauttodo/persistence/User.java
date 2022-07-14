package com.micronauttodo.persistence;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.NonNull;

import javax.validation.constraints.NotBlank;

@Introspected
public class User extends OAuthUser implements Identified {
    @NonNull
    @NotBlank
    private final String id;

    public User(@NonNull String id,
                @NonNull String iss,
                @NonNull String sub,
                @NonNull String email) {
        super(iss, sub, email);
        this.id = id;
    }

    @Override
    @NonNull
    public String getId() {
        return id;
    }
}
