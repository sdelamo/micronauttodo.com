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
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.micronaut.security.token.jwt.cookie.JwtCookieTokenReader;
import io.micronaut.views.View;
import io.swagger.v3.oas.annotations.Hidden;

import javax.annotation.security.RolesAllowed;

@Controller
class TodoListController {
    private final JwtCookieTokenReader tokenReader;
    private final TodoRepository todoRepository;

    public TodoListController(JwtCookieTokenReader tokenReader,
                              TodoRepository todoRepository) {
        this.tokenReader = tokenReader;
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
        return tokenReader.findToken(request)
                .map(token -> HttpResponse.ok(new TodoModel(token, todoRepository.findAll(oAuthUser))))
                .orElseGet(HttpResponse::serverError);
    }
}
