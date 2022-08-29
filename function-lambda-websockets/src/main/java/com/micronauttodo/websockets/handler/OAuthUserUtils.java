package com.micronauttodo.websockets.handler;

import com.micronauttodo.models.OAuthUser;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTParser;
import io.micronaut.core.annotation.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.util.Optional;

public final class OAuthUserUtils {
    private static final Logger LOG = LoggerFactory.getLogger(OAuthUserUtils.class);

    private static final String CLAIM_EMAIL = "email";

    private OAuthUserUtils() {
    }

    @NonNull
    public static Optional<OAuthUser> userOfToken(@NonNull String token) {
        try {
            JWT jwt = JWTParser.parse(token);
            JWTClaimsSet claims = jwt.getJWTClaimsSet();
            return Optional.of(new OAuthUser(claims.getIssuer(),
                    claims.getSubject(),
                    claims.getStringClaim(CLAIM_EMAIL)));
        } catch (ParseException e) {

            return Optional.empty();
        }
    }
}
