package com.micronauttodo.apigateway;

import com.amazonaws.serverless.proxy.RequestReader;
import com.amazonaws.serverless.proxy.model.AwsProxyRequestContext;
import io.micronaut.http.HttpMethod;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.simple.SimpleHttpRequest;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@MicronautTest(startApplication = false)
class DefaultStageResolverTest {
    @Inject
    StageResolver stageResolver;

    @Test
    void retrieveStage() {
        AwsProxyRequestContext awsProxyRequestContext = new AwsProxyRequestContext();
        awsProxyRequestContext.setStage("bar");
        HttpRequest<?> httpRequest = new SimpleHttpRequest(HttpMethod.GET, "/foo", null);
        assertTrue(stageResolver.resolveStage(httpRequest).isEmpty());
        httpRequest.setAttribute(RequestReader.API_GATEWAY_CONTEXT_PROPERTY, awsProxyRequestContext);
        assertEquals("bar", stageResolver.resolveStage(httpRequest).get());

    }
}