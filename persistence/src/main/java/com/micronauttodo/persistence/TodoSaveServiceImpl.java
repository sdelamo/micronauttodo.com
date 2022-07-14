package com.micronauttodo.persistence;

import io.micronaut.core.annotation.NonNull;
import jakarta.inject.Singleton;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Singleton
public class TodoSaveServiceImpl implements TodoSaveService {

    private final IdGenerator idGenerator;
    private final TodoRepository todoRepository;

    public TodoSaveServiceImpl(IdGenerator idGenerator,
                               TodoRepository todoRepository) {
        this.idGenerator = idGenerator;
        this.todoRepository = todoRepository;
    }

    @Override
    @NonNull
    public String save(@NonNull @NotNull @Valid TodoCreate todoCreate,
                     @NonNull @NotNull @Valid OAuthUser user) {
        String id = idGenerator.generate();
        todoRepository.save(new Todo(id, todoCreate.getTask()), user);
        return id;
    }
}
