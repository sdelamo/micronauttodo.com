package com.micronauttodo.controllers;

import com.micronauttodo.models.OAuthUser;
import com.micronauttodo.models.Todo;
import com.micronauttodo.models.TodoCreate;
import com.micronauttodo.services.TodoSaveService;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Consumes;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.uri.UriBuilder;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.micronaut.views.turbo.TurboStream;
import io.micronaut.views.turbo.http.TurboMediaType;
import io.swagger.v3.oas.annotations.Hidden;

import javax.validation.constraints.NotBlank;
import java.util.Collections;

@Controller
public class TodoSaveController {

    private final TodoSaveService todoSaveService;

    public TodoSaveController(TodoSaveService todoSaveService) {
        this.todoSaveService = todoSaveService;
    }

    @Hidden
    @Secured(SecurityRule.IS_AUTHENTICATED)
    @Produces(MediaType.TEXT_HTML)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @ExecuteOn(TaskExecutors.IO)
    @Post("/todo")
    HttpResponse<?> save(//@NonNull @NotNull @Valid TodoCreate todo,
                         @NonNull @NotBlank String task,
                         @NonNull OAuthUser oAuthUser,
                         HttpRequest<?> request) {
        // Workaround due to lambda binding issue
        TodoCreate todo = new TodoCreate(task.startsWith("task=") ? task.substring("task=".length()) : task);
        String todoId = todoSaveService.save(todo, oAuthUser);
        if (TurboMediaType.acceptsTurboStream(request)) {
            return HttpResponse.ok(TurboStream.builder()
                    .template("/todo/_tr.html", Collections.singletonMap("todo", new Todo(todoId, todo.getTask())))
                    .targetDomId("todos-rows")
                    .append());
        }
        return HttpResponse.seeOther(UriBuilder.of("/todo").build());
    }
}
