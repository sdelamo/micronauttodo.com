package com.micronauttodo.persistence;

import io.micronaut.core.annotation.NonNull;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

public interface UserRepository {
    void save(@NonNull @NotNull @Valid OAuthUser user);

    void delete(@NonNull @NotNull @Valid OAuthUser user);
}
