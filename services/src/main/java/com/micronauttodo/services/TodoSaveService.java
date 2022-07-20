package com.micronauttodo.services;

import com.micronauttodo.models.OAuthUser;
import com.micronauttodo.models.TodoCreate;
import io.micronaut.core.annotation.NonNull;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public interface TodoSaveService {
    @NonNull
    String save(@NonNull @NotNull @Valid TodoCreate todoCreate,
                @NonNull @NotNull @Valid OAuthUser user);
}
