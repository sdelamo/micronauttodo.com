package com.micronauttodo.apigateway;

import io.micronaut.context.annotation.Replaces;
import io.micronaut.http.server.HttpServerConfiguration;
import io.micronaut.views.thymeleaf.LinkBuilder;
import io.micronaut.views.thymeleaf.WebEngineContext;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.context.IExpressionContext;

import java.util.Map;

@Singleton
@Replaces(LinkBuilder.class)
public class LinkBuilderReplacement extends LinkBuilder {
    private static final Logger LOG = LoggerFactory.getLogger(LinkBuilderReplacement.class);
    private static final String SLASH = "/";
    private final AmazonApiGatewayHttpHostResolver amazonApiGatewayHttpHostResolver;
    private final StageResolver stageResolver;

    public LinkBuilderReplacement(HttpServerConfiguration httpServerConfiguration,
                                  AmazonApiGatewayHttpHostResolver amazonApiGatewayHttpHostResolver,
                                  StageResolver stageResolver) {
        super(httpServerConfiguration);
        this.amazonApiGatewayHttpHostResolver = amazonApiGatewayHttpHostResolver;
        this.stageResolver = stageResolver;
    }

    @Override
    protected String computeContextPath(IExpressionContext context, String base, Map<String, Object> parameters) {
        String contextPath = super.computeContextPath(context, base, parameters);
        if (context instanceof WebEngineContext) {
            WebEngineContext webEngineContext = (WebEngineContext) context;
            if (amazonApiGatewayHttpHostResolver.isAmazonApiGatewayHost(webEngineContext.getRequest())) {
                contextPath = stageResolver.resolveStage(webEngineContext.getRequest())
                        .map(stage -> stage.startsWith(SLASH) ? stage : SLASH + stage)
                        .orElse(contextPath);
            }
        }
        return contextPath;
    }
}
