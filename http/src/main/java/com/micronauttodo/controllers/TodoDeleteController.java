package com.micronauttodo.controllers;

import com.micronauttodo.persistence.OAuthUser;
import com.micronauttodo.persistence.TodoRepository;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Consumes;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.uri.UriBuilder;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;

import javax.validation.constraints.NotBlank;

@Controller
class TodoDeleteController {

    private final TodoRepository repository;

    TodoDeleteController(TodoRepository repository) {
        this.repository = repository;
    }

    @Post("/todo/{id}/delete")
    @Secured(SecurityRule.IS_AUTHENTICATED)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    HttpResponse<?> delete(@NonNull @NotBlank @PathVariable String id,
                           @NonNull OAuthUser oAuthUser) {
        repository.delete(id, oAuthUser);
        return HttpResponse.seeOther(UriBuilder.of("/todo").build());
    }
}
