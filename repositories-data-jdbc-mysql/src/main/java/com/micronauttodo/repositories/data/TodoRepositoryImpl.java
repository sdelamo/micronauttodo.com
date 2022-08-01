package com.micronauttodo.repositories.data;

import com.micronauttodo.models.OAuthUser;
import com.micronauttodo.models.Todo;
import com.micronauttodo.repositories.TodoRepository;
import com.micronauttodo.repositories.data.entities.mappers.OAuthUserEntityMapper;
import com.micronauttodo.repositories.data.entities.TodoEntity;
import com.micronauttodo.repositories.data.entities.mappers.TodoEntityMapper;
import io.micronaut.core.annotation.NonNull;
import jakarta.inject.Singleton;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;

@Singleton
public class TodoRepositoryImpl implements TodoRepository  {
    private final TodoJdbcRepository repository;

    public TodoRepositoryImpl(TodoJdbcRepository repository) {
        this.repository = repository;
    }

    @Override
    public void delete(@NonNull @NotBlank String id,
                       @NonNull @NotNull @Valid OAuthUser user) {
        repository.deleteById(id);
    }

    @Override
    public void save(@NonNull @NotNull @Valid Todo todo,
                     @NonNull @NotNull @Valid OAuthUser user) {
        repository.save(new TodoEntity(todo.getId(), todo.getTask(),
                OAuthUserEntityMapper.of(user)));
    }

    @Override
    @NonNull
    public List<Todo> findAll(@NonNull @NotNull @Valid OAuthUser user) {
        return repository.findAllByUser(OAuthUserEntityMapper.of(user));
    }

    @Override
    @NonNull
    public Optional<Todo> findById(@NonNull @NotBlank String id,
                                   @NonNull @NotNull @Valid OAuthUser user) {
        return repository.findById(id).map(TodoEntityMapper::pojoFromEntity);
    }
}
