package com.micronauttodo.controllers;

import com.micronauttodo.models.OAuthUser;
import com.micronauttodo.utils.OauthUserUtils;
import io.micronaut.core.convert.ArgumentConversionContext;
import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.bind.binders.TypedRequestArgumentBinder;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.filters.SecurityFilter;
import jakarta.inject.Singleton;

import java.util.Optional;

@Singleton
public class OauthUserArgumentBinder implements TypedRequestArgumentBinder<OAuthUser> {
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
        final Optional<OAuthUser> oAuthUserOptional = OauthUserUtils.parseOAuthUser(existing.orElse(null));
        return oAuthUserOptional.isPresent() ? () -> oAuthUserOptional : BindingResult.EMPTY;
    }


}
