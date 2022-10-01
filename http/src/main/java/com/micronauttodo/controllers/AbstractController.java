package com.micronauttodo.controllers;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.server.util.HttpHostResolver;
import io.micronaut.http.uri.UriBuilder;

import java.util.function.Consumer;

public abstract class AbstractController {
    private final HttpHostResolver httpHostResolver;

    protected AbstractController(HttpHostResolver httpHostResolver) {
        this.httpHostResolver = httpHostResolver;
    }

    protected MutableHttpResponse<?> seeOther(HttpRequest<?> request, Consumer<UriBuilder> uriBuilderConsumer) {
        UriBuilder builder = UriBuilder.of(httpHostResolver.resolve(request));
        uriBuilderConsumer.accept(builder);
        return HttpResponse.seeOther(builder.build());
    }
}
