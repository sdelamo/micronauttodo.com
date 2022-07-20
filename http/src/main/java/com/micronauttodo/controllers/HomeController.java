package com.micronauttodo.controllers;

import com.micronauttodo.persistence.OAuthUser;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.uri.UriBuilder;
import io.micronaut.views.ModelAndView;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.annotation.security.PermitAll;
import java.net.URISyntaxException;
import java.util.Collections;

@PermitAll
@Controller
public class HomeController {
    @Hidden
    @Get
    HttpResponse<?> index(@Nullable OAuthUser oAuthUser) throws URISyntaxException {
        if (oAuthUser != null) {
            return HttpResponse.seeOther(UriBuilder.of("/todo")
                    .build());
        }
        return HttpResponse.ok(new ModelAndView<>("home/index.html", Collections.emptyMap()));
    }
}