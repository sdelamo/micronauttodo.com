package com.micronauttodo.controllers;

import com.micronauttodo.persistence.OAuthUser;
import com.micronauttodo.persistence.Todo;
import com.micronauttodo.persistence.TodoRepository;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Consumes;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.uri.UriBuilder;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.micronaut.views.turbo.TurboStream;
import io.micronaut.views.turbo.http.TurboMediaType;

import javax.validation.constraints.NotBlank;
import java.util.Collections;

@Controller
class TodoDeleteController {

    private final TodoRepository repository;

    TodoDeleteController(TodoRepository repository) {
        this.repository = repository;
    }

    @Post("/todo/{id}/delete")
    @Secured(SecurityRule.IS_AUTHENTICATED)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_HTML)
    @ExecuteOn(TaskExecutors.IO)
    HttpResponse<?> delete(@NonNull @NotBlank @PathVariable String id,
                           @NonNull OAuthUser oAuthUser,
                           HttpRequest<?> request) {
        repository.delete(id, oAuthUser);
        if (TurboMediaType.acceptsTurboStream(request)) {
            return HttpResponse.ok(TurboStream.builder()
                    .targetDomId("todo-" + id)
                    .remove());
        }
        return HttpResponse.seeOther(UriBuilder.of("/todo").build());
    }
}
