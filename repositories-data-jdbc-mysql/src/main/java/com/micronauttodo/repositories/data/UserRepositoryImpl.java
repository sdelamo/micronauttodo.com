package com.micronauttodo.repositories.data;

import com.micronauttodo.models.OAuthUser;
import com.micronauttodo.repositories.UserRepository;
import com.micronauttodo.repositories.data.entities.mappers.OAuthUserEntityMapper;
import io.micronaut.core.annotation.NonNull;
import jakarta.inject.Singleton;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Singleton
public class UserRepositoryImpl implements UserRepository  {
    private final UserJdbcRepository userJdbcRepository;

    public UserRepositoryImpl(UserJdbcRepository userJdbcRepository) {
        this.userJdbcRepository = userJdbcRepository;
    }

    @Override
    public void save(@NonNull @NotNull @Valid OAuthUser user) {
        userJdbcRepository.save(OAuthUserEntityMapper.of(user));
    }

    @Override
    public void delete(@NonNull @NotNull @Valid OAuthUser user) {
        userJdbcRepository.delete(OAuthUserEntityMapper.of(user));
    }
}
