package com.micronauttodo.services;

import com.micronauttodo.models.OAuthUser;
import com.micronauttodo.models.TodoCreate;
import com.micronauttodo.models.Todo;
import com.micronauttodo.repositories.TodoRepository;
import com.micronauttodo.models.events.TodoSavedEvent;
import io.micronaut.context.event.ApplicationEventPublisher;
import io.micronaut.core.annotation.NonNull;
import jakarta.inject.Singleton;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Singleton
public class TodoSaveServiceImpl implements TodoSaveService {

    private final ApplicationEventPublisher<TodoSavedEvent> applicationEventPublisher;
    private final IdGenerator idGenerator;
    private final TodoRepository todoRepository;

    public TodoSaveServiceImpl(ApplicationEventPublisher<TodoSavedEvent> applicationEventPublisher,
                               IdGenerator idGenerator,
                               TodoRepository todoRepository) {
        this.applicationEventPublisher = applicationEventPublisher;
        this.idGenerator = idGenerator;
        this.todoRepository = todoRepository;
    }

    @Override
    @NonNull
    public String save(@NonNull @NotNull @Valid TodoCreate todoCreate,
                       @NonNull @NotNull @Valid OAuthUser user) {
        String id = idGenerator.generate();
        Todo todo = new Todo(id, todoCreate.getTask());
        todoRepository.save(todo, user);
        applicationEventPublisher.publishEvent(new TodoSavedEvent(todo, user));
        return id;
    }
}
