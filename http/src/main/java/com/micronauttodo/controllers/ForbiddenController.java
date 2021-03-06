package com.micronauttodo.controllers;

import com.micronauttodo.views.Model;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.views.View;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.annotation.security.PermitAll;

@PermitAll
@Controller
public class ForbiddenController {

    @Hidden
    @View("forbidden/index.html")
    @Get("/forbidden")
    Model index() {
        return new Model();
    }
}
