package com.micronauttodo.apigateway;

import io.micronaut.context.annotation.Replaces;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.cookie.Cookie;
import io.micronaut.http.cookie.CookieConfiguration;
import io.micronaut.security.config.RedirectConfiguration;
import io.micronaut.security.token.jwt.cookie.AccessTokenCookieConfiguration;
import io.micronaut.security.token.jwt.cookie.JwtCookieClearerLogoutHandler;
import io.micronaut.security.token.jwt.cookie.RefreshTokenCookieConfiguration;
import jakarta.inject.Singleton;

import java.net.URI;
import java.net.URISyntaxException;

@Singleton
@Replaces(JwtCookieClearerLogoutHandler.class)
public class JwtCookieClearerLogoutHandlerReplacement extends JwtCookieClearerLogoutHandler {
    private final RedirectConfigurationReplacement redirectConfigurationReplacement;
    public JwtCookieClearerLogoutHandlerReplacement(AccessTokenCookieConfiguration accessTokenCookieConfiguration,
                                                    RefreshTokenCookieConfiguration refreshTokenCookieConfiguration,
                                                    RedirectConfiguration redirectConfiguration,
                                                    RedirectConfigurationReplacement redirectConfigurationReplacement) {
        super(accessTokenCookieConfiguration, refreshTokenCookieConfiguration, redirectConfiguration);
        this.redirectConfigurationReplacement = redirectConfigurationReplacement;
    }

    @Override
    public MutableHttpResponse<?> logout(HttpRequest<?> request) {
        try {
            String uri = redirectConfigurationReplacement.prefixWithStage(super.logout, request);
            MutableHttpResponse<?> response = uri == null ? HttpResponse.ok() : HttpResponse.seeOther(new URI(uri));
            clearCookie(accessTokenCookieConfiguration, response);
            if (refreshTokenCookieConfiguration != null) {
                clearCookie(refreshTokenCookieConfiguration, response);
            }
            return response;
        } catch (URISyntaxException var5) {
            return HttpResponse.serverError();
        }
    }
    private void clearCookie(CookieConfiguration cookieConfiguration, MutableHttpResponse<?> response) {
        String domain = cookieConfiguration.getCookieDomain().orElse(null);
        String path = cookieConfiguration.getCookiePath().orElse(null);
        Cookie cookie = Cookie.of(cookieConfiguration.getCookieName(), "");
        cookie.maxAge(0).domain(domain).path(path);
        response.cookie(cookie);
    }
}
