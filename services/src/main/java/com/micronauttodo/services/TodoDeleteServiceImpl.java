package com.micronauttodo.services;

import com.micronauttodo.models.events.TodoDeletedEvent;
import com.micronauttodo.models.OAuthUser;
import com.micronauttodo.repositories.TodoRepository;
import io.micronaut.context.event.ApplicationEventPublisher;
import io.micronaut.core.annotation.NonNull;
import jakarta.inject.Singleton;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Singleton
public class TodoDeleteServiceImpl implements TodoDeleteService {

    private final ApplicationEventPublisher<TodoDeletedEvent> applicationEventPublisher;
    private final TodoRepository todoRepository;

    public TodoDeleteServiceImpl(ApplicationEventPublisher<TodoDeletedEvent> applicationEventPublisher,
                                 TodoRepository todoRepository) {
        this.applicationEventPublisher = applicationEventPublisher;
        this.todoRepository = todoRepository;
    }

    @Override
    public void delete(@NonNull @NotBlank String id,
                       @NonNull @NotNull @Valid OAuthUser user) {
        todoRepository.delete(id, user);
        applicationEventPublisher.publishEvent(new TodoDeletedEvent(id, user));
    }
}
