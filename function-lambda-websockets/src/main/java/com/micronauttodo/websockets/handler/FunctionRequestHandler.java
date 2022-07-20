package com.micronauttodo.websockets.handler;

import com.amazonaws.services.lambda.runtime.events.APIGatewayV2WebSocketEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2WebSocketResponse;
import com.micronauttodo.models.OAuthUser;
import com.micronauttodo.models.WebSocketConnection;
import com.micronauttodo.repositories.WebSocketConnectionRepository;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.function.aws.MicronautRequestHandler;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FunctionRequestHandler extends MicronautRequestHandler<APIGatewayV2WebSocketEvent, APIGatewayV2WebSocketResponse> {

    @Inject
    WebSocketConnectionRepository repository;

    private static final Logger LOG = LoggerFactory.getLogger(FunctionRequestHandler.class);

    @Override
    public APIGatewayV2WebSocketResponse execute(APIGatewayV2WebSocketEvent input) {
        LOG.info("input {}", input);
        EventType.of(input.getRequestContext().getEventType()).ifPresent(eventType -> {
            switch (eventType) {
                case CONNECT:
                    repository.save(userOfInput(input), connectionOfInput(input));
                    break;
                case DISCONNECT:
                    repository.delete(userOfInput(input), connectionOfInput(input));
                    break;
                case MESSAGE:
                default:
                    break;
            }
        });

        APIGatewayV2WebSocketResponse response = new APIGatewayV2WebSocketResponse();
        response.setStatusCode(200);
        return response;
    }

    @NonNull
    private OAuthUser userOfInput(@NonNull APIGatewayV2WebSocketEvent input) {
        //TODO
        return null;
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

