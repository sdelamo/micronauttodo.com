package com.micronauttodo.controllers;

import com.micronauttodo.views.Model;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Produces;
import io.micronaut.views.View;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.annotation.security.PermitAll;

@PermitAll
@Controller
public class NotFoundController {

    @Hidden
    @Produces(MediaType.TEXT_HTML)
    @View("notFound/index.html")
    @Get("/404")
    Model index() {
        return new Model();
    }
}
