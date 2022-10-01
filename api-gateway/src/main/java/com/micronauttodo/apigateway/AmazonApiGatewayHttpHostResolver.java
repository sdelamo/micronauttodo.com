package com.micronauttodo.apigateway;

import io.micronaut.context.BeanProvider;
import io.micronaut.context.annotation.Replaces;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.server.HttpServerConfiguration;
import io.micronaut.http.server.util.DefaultHttpHostResolver;
import io.micronaut.http.server.util.HttpHostResolver;
import io.micronaut.http.uri.UriBuilder;
import io.micronaut.runtime.server.EmbeddedServer;
import jakarta.inject.Singleton;

/**
 * @see <a href="https://docs.aws.amazon.com/apigateway/latest/developerguide/how-to-call-api.html">Invoking a REST API in Amazon API Gateway</a>.
 */
@Singleton
@Replaces(HttpHostResolver.class)
public class AmazonApiGatewayHttpHostResolver extends DefaultHttpHostResolver {
    private static final String HTTPS = "https://";
    private static final String EXECUTE_API_SUBDOMAIN = ".execute-api.";
    private static final String DOMAIN_AMAZONAWS_COM = ".amazonaws.com";

    private final StageResolver stageResolver;

    /**
     * @param serverConfiguration           The server configuration
     * @param embeddedServer                The embedded server provider
     * @param stageResolver Amazon API Gateway Configuration
     */
    public AmazonApiGatewayHttpHostResolver(HttpServerConfiguration serverConfiguration,
                                            BeanProvider<EmbeddedServer> embeddedServer,
                                            StageResolver stageResolver) {
        super(serverConfiguration, embeddedServer);
        this.stageResolver = stageResolver;
    }

    @Override
    @NonNull
    public String resolve(@Nullable HttpRequest request) {
        String host = super.resolve(request);
        if (isAmazonApiGatewayHost(host)) {
            return stageResolver.resolveStage(request)
                    .map(stage ->
                            UriBuilder.of(host)
                                    .path(stage)
                                    .build()
                                    .toString()
                    ).orElse(host);
        }
        return host;
    }

    public boolean isAmazonApiGatewayHost(HttpRequest<?> request) {
        return isAmazonApiGatewayHost(super.resolve(request));
    }

    public boolean isAmazonApiGatewayHost(@NonNull String host) {
        return host.startsWith(HTTPS) &&
                host.endsWith(DOMAIN_AMAZONAWS_COM) &&
                host.contains(EXECUTE_API_SUBDOMAIN);
    }
}
