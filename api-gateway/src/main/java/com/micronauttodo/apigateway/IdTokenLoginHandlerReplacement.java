package com.micronauttodo.apigateway;

import io.micronaut.context.annotation.Replaces;
import io.micronaut.context.annotation.Requires;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.core.util.functional.ThrowingSupplier;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.config.RedirectConfiguration;
import io.micronaut.security.config.SecurityConfigurationProperties;
import io.micronaut.security.errors.PriorToLoginPersistence;
import io.micronaut.security.oauth2.endpoint.token.response.IdTokenLoginHandler;
import io.micronaut.security.token.jwt.cookie.AccessTokenCookieConfiguration;
import jakarta.inject.Singleton;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

@Requires(property = SecurityConfigurationProperties.PREFIX + ".authentication", value = "idtoken")
@Replaces(IdTokenLoginHandler.class)
@Singleton
public class IdTokenLoginHandlerReplacement extends IdTokenLoginHandler {

    private final RedirectConfigurationReplacement redirectConfigurationReplacement;
    public IdTokenLoginHandlerReplacement(AccessTokenCookieConfiguration accessTokenCookieConfiguration,
                                          RedirectConfiguration redirectConfiguration,
                                          @Nullable PriorToLoginPersistence priorToLoginPersistence,
                                          RedirectConfigurationReplacement redirectConfigurationReplacement) {
        super(accessTokenCookieConfiguration, redirectConfiguration, priorToLoginPersistence);
        this.redirectConfigurationReplacement = redirectConfigurationReplacement;
    }


    @Override
    protected MutableHttpResponse<?> createSuccessResponse(HttpRequest<?> request) {
        try {
            String success = redirectConfigurationReplacement.prefixWithStage(loginSuccess, request);
            if (success == null) {
                return HttpResponse.ok();
            }
            MutableHttpResponse<?> response = HttpResponse.status(HttpStatus.SEE_OTHER);
            ThrowingSupplier<URI, URISyntaxException> uriSupplier = () -> new URI(success);
            if (priorToLoginPersistence != null) {
                Optional<URI> originalUri = priorToLoginPersistence.getOriginalUri(request, response);
                if (originalUri.isPresent()) {
                    uriSupplier = originalUri::get;
                }
            }
            response.getHeaders().location(uriSupplier.get());
            return response;
        } catch (URISyntaxException e) {
            return HttpResponse.serverError();
        }
    }
}
