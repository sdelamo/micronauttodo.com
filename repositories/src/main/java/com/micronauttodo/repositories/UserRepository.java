package com.micronauttodo.repositories;

import com.micronauttodo.models.OAuthUser;
import io.micronaut.core.annotation.NonNull;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public interface UserRepository {
    void save(@NonNull @NotNull @Valid OAuthUser user);

    void delete(@NonNull @NotNull @Valid OAuthUser user);
}
