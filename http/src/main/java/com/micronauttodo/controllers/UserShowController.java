package com.micronauttodo.controllers;

import com.micronauttodo.views.UserModel;
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
import io.micronaut.security.token.reader.TokenResolver;
import io.micronaut.views.View;
import io.swagger.v3.oas.annotations.Hidden;

@Controller
class UserShowController {

    private final TokenResolver tokenResolver;

    UserShowController(TokenResolver tokenResolver) {
        this.tokenResolver = tokenResolver;
    }

    @Hidden
    @Secured(SecurityRule.IS_AUTHENTICATED)
    @Get("/user")
    @View("user/show.html")
    @Produces(MediaType.TEXT_HTML)
    @ExecuteOn(TaskExecutors.IO)
    HttpResponse<?> show(HttpRequest<?> request) {
        return tokenResolver.resolveToken(request)
                .map(token -> HttpResponse.ok(new UserModel(token)))
                .orElseGet(HttpResponse::serverError);
    }
}
