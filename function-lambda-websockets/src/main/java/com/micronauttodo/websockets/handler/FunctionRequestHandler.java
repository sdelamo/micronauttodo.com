package com.micronauttodo.websockets.handler;

import com.amazonaws.services.lambda.runtime.events.APIGatewayV2WebSocketEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2WebSocketResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.micronauttodo.models.OAuthUser;
import com.micronauttodo.models.WebSocketConnection;
import com.micronauttodo.repositories.WebSocketConnectionRepository;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.function.aws.MicronautRequestHandler;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Base64;
import java.util.Map;
import java.util.Optional;

public class FunctionRequestHandler extends MicronautRequestHandler<APIGatewayV2WebSocketEvent, APIGatewayV2WebSocketResponse> {

    @Inject
    WebSocketConnectionRepository repository;

    @Inject
    ObjectMapper objectMapper;

    private static final Logger LOG = LoggerFactory.getLogger(FunctionRequestHandler.class);

    @Override
    public APIGatewayV2WebSocketResponse execute(APIGatewayV2WebSocketEvent input) {
        LOG.info("input {}", input);
        processWebSocketEvent(input);
        APIGatewayV2WebSocketResponse response = new APIGatewayV2WebSocketResponse();
        response.setStatusCode(200);
        return response;
    }

    void processWebSocketEvent(APIGatewayV2WebSocketEvent input) {
        EventType.of(input.getRequestContext().getEventType()).ifPresent(eventType -> {
            switch (eventType) {
                case CONNECT:
                    userOfInput(input).ifPresent(user -> repository.save(user, connectionOfInput(input)));
                    break;
                case DISCONNECT:
                    userOfInput(input).ifPresent(user -> repository.delete(user, connectionOfInput(input)));
                    break;
                case MESSAGE:
                default:
                    break;
            }
        });
    }

    @NonNull
    private Optional<OAuthUser> userOfInput(@NonNull APIGatewayV2WebSocketEvent input) {
        try {
            String token = input.getQueryStringParameters().get("JWT");
            return Optional.of(userOfToken(token));
        } catch (JsonProcessingException e) {
            return Optional.empty();
        }
    }

    @NonNull
    private OAuthUser userOfToken(@NonNull String token) throws JsonProcessingException {
        String[] chunks = token.split("\\.");
        Base64.Decoder decoder = Base64.getUrlDecoder();
        String payload = new String(decoder.decode(chunks[1]));
        Map<?, ?> claims = objectMapper.readValue(payload, Map.class);
        return new OAuthUser(claims.get("iss").toString(),
                claims.get("sub").toString(),
                claims.get("email").toString());
    }

    @NonNull
    private WebSocketConnection connectionOfInput(APIGatewayV2WebSocketEvent input) {
        return new WebSocketConnection(System.getenv("AWS_REGION"),
                input.getRequestContext().getApiId(),
                input.getRequestContext().getStage(),
                input.getRequestContext().getConnectionId(),
                input.getRequestContext().getDomainName());
    }

}

