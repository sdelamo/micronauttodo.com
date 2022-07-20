package com.micronauttodo.controllers;

import com.micronauttodo.views.UserModel;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.micronaut.security.token.jwt.cookie.AccessTokenCookieConfiguration;
import io.micronaut.security.token.jwt.cookie.JwtCookieTokenReader;
import io.micronaut.views.View;
import io.swagger.v3.oas.annotations.Hidden;

@Controller
class UserShowController {

    private final JwtCookieTokenReader tokenReader;

    UserShowController(JwtCookieTokenReader tokenReader) {
        this.tokenReader = tokenReader;
    }

    @Hidden
    @Secured(SecurityRule.IS_AUTHENTICATED)
    @Get("/user")
    @View("user/show.html")
    @ExecuteOn(TaskExecutors.IO)
    HttpResponse<?> show(HttpRequest<?> request) {
        return tokenReader.findToken(request)
                .map(token -> HttpResponse.ok(new UserModel(token)))
                .orElseGet(HttpResponse::serverError);
    }
}
