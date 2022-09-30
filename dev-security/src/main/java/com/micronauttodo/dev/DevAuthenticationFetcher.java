package com.micronauttodo.dev;

import io.micronaut.context.annotation.Requires;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.async.publisher.Publishers;
import io.micronaut.http.HttpRequest;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.filters.AuthenticationFetcher;
import io.micronaut.security.oauth2.configuration.OauthClientConfiguration;
import io.micronaut.security.token.jwt.generator.AccessTokenConfiguration;
import io.micronaut.security.token.jwt.generator.claims.ClaimsGenerator;
import io.micronaut.security.token.jwt.generator.claims.JwtClaims;
import jakarta.inject.Singleton;
import org.reactivestreams.Publisher;
import java.util.Map;

@Requires(missingBeans = OauthClientConfiguration.class)
@Singleton
public class DevAuthenticationFetcher implements AuthenticationFetcher {

    private final AccessTokenConfiguration accessTokenConfiguration;
    private final ClaimsGenerator claimsGenerator;
    private static final String ID = "014e7c43-ff5c-23e7-4506-124fe64d2303";

    public DevAuthenticationFetcher(AccessTokenConfiguration accessTokenConfiguration,
                                    ClaimsGenerator claimsGenerator) {
        this.accessTokenConfiguration = accessTokenConfiguration;
        this.claimsGenerator = claimsGenerator;
    }

    @Override
    public Publisher<Authentication> fetchAuthentication(HttpRequest<?> request) {
        return Publishers.just(new Authentication() {
            @Override
            @NonNull
            public Map<String, Object> getAttributes() {
                return claims();
            }
            @Override
            public String getName() {
                return ID;
            }
        });
    }

    public Map<String, Object> claims() {
        return claimsGenerator.generateClaimsSet(oldClaims(), accessTokenConfiguration.getExpiration());
    }

    public Map<String, Object> oldClaims() {
        return Map.of(
                JwtClaims.ISSUER, "https://cognito-idp.us-east-1.amazonaws.com/us-east-1_abcdemu6o0#",
                JwtClaims.SUBJECT, ID,
                "email", "john@email.com"
        );
    }
}

