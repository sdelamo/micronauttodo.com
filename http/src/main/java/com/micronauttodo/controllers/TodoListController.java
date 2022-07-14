package com.micronauttodo.controllers;

import com.micronauttodo.persistence.OAuthUser;
import com.micronauttodo.persistence.TodoRepository;
import com.micronauttodo.views.TodoModel;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.micronaut.views.View;
import java.util.HashMap;
import java.util.Map;

@Controller
class TodoListController {
    private final TodoRepository todoRepository;

    public TodoListController(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    @Secured(SecurityRule.IS_AUTHENTICATED)
    @Get("/todo")
    @View("todo/index.html")
    TodoModel index(@NonNull OAuthUser oAuthUser) {
        return new TodoModel(todoRepository.findAll(oAuthUser));
    }
}
