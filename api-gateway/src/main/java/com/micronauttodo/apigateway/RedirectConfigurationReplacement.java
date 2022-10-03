package com.micronauttodo.apigateway;

import io.micronaut.context.annotation.Replaces;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.context.ServerRequestContext;
import io.micronaut.security.config.ForbiddenRedirectConfiguration;
import io.micronaut.security.config.RedirectConfiguration;
import io.micronaut.security.config.RedirectConfigurationProperties;
import io.micronaut.security.config.RefreshRedirectConfiguration;
import io.micronaut.security.config.UnauthorizedRedirectConfiguration;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Replaces(RedirectConfiguration.class)
@Singleton
public class RedirectConfigurationReplacement implements RedirectConfiguration {
    private static final Logger LOG = LoggerFactory.getLogger(RedirectConfigurationReplacement.class);
    private static final String SLASH = "/";
    private final RedirectConfigurationProperties redirectConfigurationProperties;
    private final StageResolver stageResolver;

    public RedirectConfigurationReplacement(RedirectConfigurationProperties redirectConfigurationProperties,
                                            StageResolver stageResolver) {
        this.redirectConfigurationProperties = redirectConfigurationProperties;
        this.stageResolver = stageResolver;
    }

    @Override
    @NonNull
    public String getLoginSuccess() {
        return prefixWithStage(redirectConfigurationProperties.getLoginSuccess());
    }

    @Override
    @NonNull
    public String getLoginFailure() {
        return prefixWithStage(redirectConfigurationProperties.getLoginFailure());
    }

    @Override
    @NonNull
    public String getLogout() {
        return prefixWithStage(redirectConfigurationProperties.getLogout());
    }

    @Override
    @NonNull
    public UnauthorizedRedirectConfiguration getUnauthorized() {
        return new UnauthorizedRedirectConfiguration() {
            @Override
            public boolean isEnabled() {
                return redirectConfigurationProperties.getUnauthorized().isEnabled();
            }
            @Override
            @NonNull
            public String getUrl() {
                return prefixWithStage(redirectConfigurationProperties.getUnauthorized().getUrl());
            }
        };
    }

    @Override
    @NonNull
    public ForbiddenRedirectConfiguration getForbidden() {
        return new ForbiddenRedirectConfiguration() {
            @Override
            public boolean isEnabled() {
                return redirectConfigurationProperties.getForbidden().isEnabled();
            }
            @Override
            @NonNull
            public String getUrl() {
                return prefixWithStage(redirectConfigurationProperties.getForbidden().getUrl());
            }
        };
    }

    @Override
    @NonNull
    public RefreshRedirectConfiguration getRefresh() {
        return new RefreshRedirectConfiguration() {
            @Override
            public boolean isEnabled() {
                return redirectConfigurationProperties.getRefresh().isEnabled();
            }
            @Override
            @NonNull
            public String getUrl() {
                return prefixWithStage(redirectConfigurationProperties.getRefresh().getUrl());
            }
        };
    }

    @Override
    public boolean isPriorToLogin() {
        return redirectConfigurationProperties.isPriorToLogin();
    }

    public String prefixWithStage(String str) {
        LOG.info("prefix without stage {} request found {}", str, ServerRequestContext.currentRequest().isPresent());
        return ServerRequestContext.currentRequest()
                .flatMap(stageResolver::resolveStage)
                .map(stage -> stage.startsWith(SLASH) ? stage : SLASH + stage)
                .map(stage -> stage + str)
                .orElse(str);
    }

    public String prefixWithStage(String str, HttpRequest<?> request) {
        return stageResolver.resolveStage(request)
                .map(stage -> stage.startsWith(SLASH) ? stage : SLASH + stage)
                .map(stage -> stage + str)
                .orElse(str);
    }
}
