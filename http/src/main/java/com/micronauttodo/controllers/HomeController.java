package com.micronauttodo.controllers;

import com.micronauttodo.models.OAuthUser;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.server.util.HttpHostResolver;
import io.micronaut.http.uri.UriBuilder;
import io.micronaut.views.ModelAndView;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.annotation.security.PermitAll;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URISyntaxException;
import java.util.Collections;

@PermitAll
@Controller
public class HomeController extends AbstractController{
    public HomeController(HttpHostResolver httpHostResolver) {
        super(httpHostResolver);
    }

    @Hidden
    @Get
    HttpResponse<?> index(@Nullable OAuthUser oAuthUser, HttpRequest<?> request) throws URISyntaxException {
        if (oAuthUser != null) {
            return seeOther(request, uriBuilder -> uriBuilder.path("todo"));
        }
        return HttpResponse.ok(new ModelAndView<>("home/index.html", Collections.emptyMap()));
    }
}