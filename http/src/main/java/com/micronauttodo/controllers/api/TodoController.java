package com.micronauttodo.controllers.api;

import com.micronauttodo.models.OAuthUser;
import com.micronauttodo.models.Todo;
import com.micronauttodo.models.TodoCreate;
import com.micronauttodo.repositories.TodoRepository;
import com.micronauttodo.services.TodoSaveService;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.http.HttpHeaders;
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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.links.Link;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.net.URI;
import java.util.List;
import java.util.Optional;

@Secured(SecurityRule.IS_AUTHENTICATED)
@ExecuteOn(TaskExecutors.IO)
@Controller(Api.PATH + "/todo")
public class TodoController {
    private final TodoSaveService todoSaveService;
    private final TodoRepository todoRepository;

    public TodoController(TodoSaveService todoSaveService,
                          TodoRepository todoRepository) {
        this.todoSaveService = todoSaveService;
        this.todoRepository = todoRepository;
    }

    @Operation(operationId = "todo-save",
            parameters = { @Parameter(name = "JWT", in = ParameterIn.COOKIE) },
            requestBody = @RequestBody(content = @Content(schema = @Schema(implementation = TodoCreate.class))),
    responses = @ApiResponse(responseCode="201",
            links = { @Link(operationId = "todo-show") },
            headers = { @Header(name = HttpHeaders.LOCATION) }))
    @Post
    HttpResponse<?> save(@NonNull @NotNull @Body TodoCreate todo,
                         @NonNull OAuthUser user) {
        return HttpResponse.created(location(todoSaveService.save(todo, user)));
    }

    @Operation(operationId = "todo-show",
            parameters = { @Parameter(name = "id", in = ParameterIn.PATH), @Parameter(name = "JWT", in = ParameterIn.COOKIE) })
    @Get("/{id}")
    Optional<Todo> show(@NonNull @NotBlank @PathVariable String id,
                        @NonNull OAuthUser user) {
        return todoRepository.findById(id, user);
    }

    @Operation(operationId = "todo-delete",
            parameters = { @Parameter(name = "id", in = ParameterIn.PATH), @Parameter(name = "JWT", in = ParameterIn.COOKIE) })
    @Status(HttpStatus.NO_CONTENT)
    @Delete("/{id}")
    void delete(@NonNull @NotBlank @PathVariable String id,
                        @NonNull OAuthUser user) {
        todoRepository.delete(id, user);
    }

    @Operation(operationId = "todo-index",
            parameters = { @Parameter(name = "JWT", in = ParameterIn.COOKIE) })
    @Get
    List<Todo> index(@NonNull OAuthUser user) {
        return todoRepository.findAll(user);
    }

    @NonNull
    private URI location(@NonNull String id) {
        return UriBuilder.of("/api")
                .path("v1")
                .path(id)
                .build();
    }
}
