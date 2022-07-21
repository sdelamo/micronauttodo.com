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
public class UnauthorizedController {

    @Hidden
    @View("unauthorized/index.html")
    @Produces(MediaType.TEXT_HTML)
    @Get("/unauthorized")
    Model index() {
        return new Model();
    }
}
