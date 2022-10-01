package com.micronauttodo.views;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.NonNull;

import javax.validation.constraints.NotBlank;

@Introspected
public class UserModel extends Model {

    @NonNull
    @NotBlank
    private final String accessToken;

    public UserModel(@NonNull String host,
                     @NonNull String accessToken) {
        super(host);
        this.accessToken = accessToken;
    }

    @NonNull
    public String getAccessToken() {
        return accessToken;
    }
}
