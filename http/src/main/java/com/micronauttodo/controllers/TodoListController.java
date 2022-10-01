package com.micronauttodo.controllers;

import com.micronauttodo.models.OAuthUser;
import com.micronauttodo.repositories.TodoRepository;
import com.micronauttodo.views.TodoModel;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.server.util.HttpHostResolver;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.micronaut.security.token.reader.TokenResolver;
import io.micronaut.views.View;
import io.swagger.v3.oas.annotations.Hidden;

@Controller
class TodoListController {
    private final HttpHostResolver httpHostResolver;
    private final TokenResolver tokenResolver;
    private final TodoRepository todoRepository;

    public TodoListController(HttpHostResolver httpHostResolver,
                              TokenResolver tokenResolver,
                              TodoRepository todoRepository) {
        this.httpHostResolver = httpHostResolver;
        this.tokenResolver = tokenResolver;
        this.todoRepository = todoRepository;
    }

    @Hidden
    @Secured(SecurityRule.IS_AUTHENTICATED)
    @Get("/todo")
    @View("todo/index.html")
    @Produces(MediaType.TEXT_HTML)
    @ExecuteOn(TaskExecutors.IO)
    HttpResponse<?> index(@NonNull HttpRequest<?> request,
                    @NonNull OAuthUser oAuthUser) {
        return tokenResolver.resolveToken(request)
                .map(token -> HttpResponse.ok(new TodoModel(httpHostResolver.resolve(request), token, todoRepository.findAll(oAuthUser))))
                .orElseGet(HttpResponse::serverError);
    }
}
