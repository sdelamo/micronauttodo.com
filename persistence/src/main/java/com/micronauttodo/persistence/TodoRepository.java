package com.micronauttodo.persistence;

import io.micronaut.core.annotation.NonNull;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;

public interface TodoRepository {
    void delete(@NonNull @NotBlank String id,
                @NonNull @NotNull @Valid OAuthUser user);

    void save(@NonNull @NotNull @Valid Todo todo,
              @NonNull @NotNull @Valid OAuthUser user);

    @NonNull
    List<Todo> findAll(@NonNull @NotNull @Valid OAuthUser oAuthUser);

    @NonNull
    Optional<Todo> findById(@NonNull @NotBlank String id,
                            @NonNull @NotNull @Valid OAuthUser user);
}
