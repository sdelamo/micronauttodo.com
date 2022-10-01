package com.micronauttodo.apigateway;

import io.micronaut.context.annotation.DefaultImplementation;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.HttpRequest;
import java.util.Optional;

@DefaultImplementation(DefaultStageResolver.class)
@FunctionalInterface
public interface StageResolver {

    @NonNull
    Optional<String> resolveStage(@Nullable HttpRequest<?> request);
}
