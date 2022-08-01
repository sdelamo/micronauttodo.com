package com.micronauttodo.repositories.data.entities.mappers;

import com.micronauttodo.models.Todo;
import com.micronauttodo.repositories.data.entities.TodoEntity;
import io.micronaut.core.annotation.NonNull;

public final class TodoEntityMapper {
    private TodoEntityMapper() {

    }

    @NonNull
    public static Todo pojoFromEntity(@NonNull TodoEntity entity) {
        return new Todo(entity.getId(), entity.getTask());
    }
}
