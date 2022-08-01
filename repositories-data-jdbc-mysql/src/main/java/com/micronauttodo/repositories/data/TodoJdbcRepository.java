package com.micronauttodo.repositories.data;

import com.micronauttodo.models.Todo;
import com.micronauttodo.repositories.data.entities.OAuthUserEntity;
import com.micronauttodo.repositories.data.entities.TodoEntity;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;
import io.micronaut.data.repository.GenericRepository;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;

@JdbcRepository(dialect = Dialect.MYSQL)
public interface TodoJdbcRepository extends GenericRepository<TodoEntity, String> {

    @NonNull
    TodoEntity save(@Valid @NotNull @NonNull TodoEntity entity);

    @NonNull
    List<Todo> findAllByUser(@NonNull @NotNull @Valid OAuthUserEntity user);

    void deleteById(@NonNull @NotNull String id);

    Optional<TodoEntity> findById(@NonNull @NotNull String id);
}
