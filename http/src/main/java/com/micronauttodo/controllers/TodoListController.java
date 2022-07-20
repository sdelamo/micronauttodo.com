package com.micronauttodo.controllers;

import com.micronauttodo.models.OAuthUser;
import com.micronauttodo.repositories.TodoRepository;
import com.micronauttodo.views.TodoModel;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.micronaut.views.View;
import io.swagger.v3.oas.annotations.Hidden;

@Controller
class TodoListController {
    private final TodoRepository todoRepository;

    public TodoListController(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    @Hidden
    @Secured(SecurityRule.IS_AUTHENTICATED)
    @Get("/todo")
    @View("todo/index.html")
    @ExecuteOn(TaskExecutors.IO)
    TodoModel index(@NonNull OAuthUser oAuthUser) {
        return new TodoModel(todoRepository.findAll(oAuthUser));
    }
}
