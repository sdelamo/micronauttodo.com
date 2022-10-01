package com.micronauttodo.apigateway;

import com.amazonaws.serverless.proxy.RequestReader;
import com.amazonaws.serverless.proxy.model.AwsProxyRequestContext;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.HttpRequest;
import jakarta.inject.Singleton;

import java.util.Optional;

@Singleton
public class DefaultStageResolver implements StageResolver {
    @Override
    @NonNull
    public Optional<String> resolveStage(@Nullable HttpRequest<?> request) {
        return Optional.ofNullable(request)
                .flatMap(req -> req.getAttribute(RequestReader.API_GATEWAY_CONTEXT_PROPERTY, AwsProxyRequestContext.class))
                .map(AwsProxyRequestContext::getStage);
    }
}
