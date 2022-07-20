package com.micronauttodo.controllers.api;

import com.micronauttodo.persistence.OAuthUser;
import com.micronauttodo.persistence.Todo;
import com.micronauttodo.persistence.TodoCreate;
import com.micronauttodo.persistence.TodoRepository;
import com.micronauttodo.persistence.TodoSaveService;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Status;
import io.micronaut.http.uri.UriBuilder;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.net.URI;
import java.util.List;
import java.util.Optional;

@Secured(SecurityRule.IS_AUTHENTICATED)
@ExecuteOn(TaskExecutors.IO)
@Controller("/api/v1/todo")
public class TodoController {
    private final TodoSaveService todoSaveService;
    private final TodoRepository todoRepository;

    public TodoController(TodoSaveService todoSaveService,
                          TodoRepository todoRepository) {
        this.todoSaveService = todoSaveService;
        this.todoRepository = todoRepository;
    }

    @Post
    HttpResponse<?> save(@NonNull @NotNull @Body TodoCreate todo,
                         @NonNull OAuthUser user) {
        String todoId = todoSaveService.save(todo, user);
        URI todoLocation = UriBuilder.of("/api")
                .path("v1")
                .path(todoId)
                .build();
        return HttpResponse.created(todoLocation);
    }

    @Get("/{id}")
    Optional<Todo> show(@NonNull @NotBlank @PathVariable String id,
                        @NonNull OAuthUser user) {
        return todoRepository.findById(id, user);
    }

    @Status(HttpStatus.NO_CONTENT)
    @Delete("/{id}")
    void delete(@NonNull @NotBlank @PathVariable String id,
                        @NonNull OAuthUser user) {
        todoRepository.delete(id, user);
    }

    @Get
    List<Todo> index(@NonNull OAuthUser user) {
        return todoRepository.findAll(user);
    }
}
