package com.micronauttodo.controllers;

import com.micronauttodo.models.OAuthUser;
import com.micronauttodo.services.TodoDeleteService;
import com.micronauttodo.utils.TurboUtils;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Consumes;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.server.util.HttpHostResolver;
import io.micronaut.http.uri.UriBuilder;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.micronaut.views.turbo.http.TurboMediaType;
import io.swagger.v3.oas.annotations.Hidden;

import javax.validation.constraints.NotBlank;

@Controller
class TodoDeleteController extends AbstractController {
    private final TodoDeleteService todoDeleteService;

    TodoDeleteController(HttpHostResolver httpHostResolver, TodoDeleteService todoDeleteService) {
        super(httpHostResolver);
        this.todoDeleteService = todoDeleteService;
    }

    @Hidden
    @Post("/todo/{id}/delete")
    @Secured(SecurityRule.IS_AUTHENTICATED)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_HTML)
    @ExecuteOn(TaskExecutors.IO)
    HttpResponse<?> delete(@NonNull @NotBlank @PathVariable String id,
                           @NonNull OAuthUser oAuthUser,
                           HttpRequest<?> request) {
        todoDeleteService.delete(id, oAuthUser);
        return TurboMediaType.acceptsTurboStream(request) ?
                HttpResponse.ok(TurboUtils.remove(id)) :
                seeOther(request, builder -> builder.path("todo"));
    }
}
