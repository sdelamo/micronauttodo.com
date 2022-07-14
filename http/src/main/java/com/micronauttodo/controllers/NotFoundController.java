package com.micronauttodo.controllers;

import com.micronauttodo.views.Model;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.views.View;
import jakarta.annotation.security.PermitAll;

import java.util.Collections;
import java.util.Map;

@PermitAll
@Controller
public class NotFoundController {

    @View("notFound/index.html")
    @Get("/404")
    Model index() {
        return new Model();
    }
}
