package com.micronauttodo.views;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;

@Introspected
public class Model {

    @Nullable
    private String email;

    @NonNull
    private String assetsUrl = "";

    @Nullable
    public String getEmail() {
        return email;
    }

    public void setEmail(@Nullable String email) {
        this.email = email;
    }

    @NonNull
    public String getAssetsUrl() {
        return assetsUrl;
    }

    public void setAssetsUrl(@NonNull String assetsUrl) {
        this.assetsUrl = assetsUrl;
    }
}
