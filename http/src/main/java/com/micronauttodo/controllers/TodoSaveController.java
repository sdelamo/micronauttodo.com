package com.micronauttodo.controllers;

import com.micronauttodo.persistence.OAuthUser;
import com.micronauttodo.persistence.TodoCreate;
import com.micronauttodo.persistence.TodoSaveService;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Consumes;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.uri.UriBuilder;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Controller
public class TodoSaveController {

    private final TodoSaveService todoSaveService;

    public TodoSaveController(TodoSaveService todoSaveService) {
        this.todoSaveService = todoSaveService;
    }

    @Secured(SecurityRule.IS_AUTHENTICATED)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Post("/todo")
    HttpResponse<?> save(//@NonNull @NotNull @Valid TodoCreate todo,
                         @NonNull @NotBlank String task,
                         @NonNull OAuthUser oAuthUser) {
        // Workaround due to lambda binding issue
        TodoCreate todo = new TodoCreate(task.startsWith("task=") ? task.substring("task=".length()) : task);
        todoSaveService.save(todo, oAuthUser);
        return HttpResponse.seeOther(UriBuilder.of("/todo").build());
    }
}
