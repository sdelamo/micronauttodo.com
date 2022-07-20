package com.micronauttodo.services;

import com.micronauttodo.models.OAuthUser;
import io.micronaut.core.annotation.NonNull;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@FunctionalInterface
public interface TodoDeleteService {
    void delete(@NonNull @NotBlank String id,
                @NonNull @NotNull @Valid OAuthUser user);
}
