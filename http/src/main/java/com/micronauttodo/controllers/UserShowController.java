package com.micronauttodo.controllers;

import com.micronauttodo.views.UserModel;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.cookie.Cookie;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.micronaut.security.token.jwt.cookie.AccessTokenCookieConfiguration;
import io.micronaut.views.View;

@Controller
class UserShowController {

    private final AccessTokenCookieConfiguration accessTokenCookieConfiguration;

    UserShowController(AccessTokenCookieConfiguration accessTokenCookieConfiguration) {
        this.accessTokenCookieConfiguration = accessTokenCookieConfiguration;
    }

    @Secured(SecurityRule.IS_AUTHENTICATED)
    @Get("/user")
    @View("user/show.html")
    @ExecuteOn(TaskExecutors.IO)
    HttpResponse<?> show(HttpRequest<?> request) {
        return request.getCookies().findCookie(accessTokenCookieConfiguration.getCookieName())
                .map(cookie -> HttpResponse.ok(new UserModel(cookie.getValue())))
                .orElseGet(HttpResponse::serverError);
    }
}
