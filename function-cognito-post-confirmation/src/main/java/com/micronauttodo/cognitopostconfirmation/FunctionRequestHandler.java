package com.micronauttodo.cognitopostconfirmation;

import com.amazonaws.services.lambda.runtime.events.CognitoUserPoolPostConfirmationEvent;
import com.micronauttodo.models.OAuthUser;
import com.micronauttodo.repositories.UserRepository;
import io.micronaut.function.aws.MicronautRequestHandler;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FunctionRequestHandler
        extends MicronautRequestHandler<CognitoUserPoolPostConfirmationEvent, CognitoUserPoolPostConfirmationEvent> {
    private static final Logger LOG = LoggerFactory.getLogger(FunctionRequestHandler.class);

    @Inject
    UserRepository userRepository;

    @Override
    public CognitoUserPoolPostConfirmationEvent execute(CognitoUserPoolPostConfirmationEvent input) {
        LOG.info("client metadata {}", input.getRequest().getClientMetadata());
        LOG.info("user metadata {}", input.getRequest().getUserAttributes());

        String issuer = "https://cognito-idp." + input.getRegion() + ".amazonaws.com/" + input.getUserPoolId();
        String sub = input.getRequest().getUserAttributes().get("sub");
        String email = input.getRequest().getUserAttributes().get("email");
        LOG.info("issuer {}", issuer);
        LOG.info("sub {}", sub);
        LOG.info("email {}", email);


        userRepository.save(new OAuthUser(issuer, sub, email));
        return input;
    }
}
