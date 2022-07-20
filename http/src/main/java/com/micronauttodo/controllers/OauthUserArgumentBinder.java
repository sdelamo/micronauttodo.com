package com.micronauttodo.controllers;

import com.micronauttodo.models.OAuthUser;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.core.convert.ArgumentConversionContext;
import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.bind.binders.TypedRequestArgumentBinder;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.filters.SecurityFilter;
import io.micronaut.security.token.jwt.generator.claims.JwtClaims;
import jakarta.inject.Singleton;

import java.util.Optional;

@Singleton
public class OauthUserArgumentBinder implements TypedRequestArgumentBinder<OAuthUser> {
    public static final String CLAIM_EMAIL = "email";
    private final Argument<OAuthUser> argumentType;

    protected OauthUserArgumentBinder() {
        argumentType = Argument.of(OAuthUser.class);
    }

    @Override
    public Argument<OAuthUser> argumentType() {
        return argumentType;
    }

    @Override
    public BindingResult<OAuthUser> bind(ArgumentConversionContext<OAuthUser> context, HttpRequest<?> source) {
        if (!source.getAttributes().contains(SecurityFilter.KEY)) {
            return BindingResult.UNSATISFIED;
        }

        final Optional<Authentication> existing = source.getUserPrincipal(Authentication.class);
        final Optional<OAuthUser> oAuthUserOptional = parseOAuthUser(existing.orElse(null));
        return oAuthUserOptional.isPresent() ? () -> oAuthUserOptional : BindingResult.EMPTY;
    }

    @NonNull
    private Optional<OAuthUser> parseOAuthUser(@Nullable Authentication authentication) {
        Optional<OAuthUser> oAuthUserOptional = Optional.empty();
        if (authentication != null) {
            oAuthUserOptional = Optional.of(new OAuthUser(authentication.getAttributes().get(JwtClaims.ISSUER).toString(),
                    authentication.getAttributes().get(JwtClaims.SUBJECT).toString(),
                    authentication.getAttributes().get(CLAIM_EMAIL).toString()));
        }
        return oAuthUserOptional;
    }
}
