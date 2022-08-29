package com.micronauttodo.websockets.handler;

import com.amazonaws.services.lambda.runtime.events.APIGatewayV2WebSocketEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2WebSocketResponse;
import com.micronauttodo.models.OAuthUser;
import com.micronauttodo.models.WebSocketConnection;
import com.micronauttodo.repositories.WebSocketConnectionRepository;
import io.micronaut.context.ApplicationContextBuilder;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.function.aws.MicronautRequestHandler;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Optional;

public class FunctionRequestHandler extends MicronautRequestHandler<APIGatewayV2WebSocketEvent, APIGatewayV2WebSocketResponse> {

    private static final String QUERY_STRING_PARAM_TOKEN = "token";
    public static final String US_EAST_1 = "us-east-1";
    public static final String DEFAULT_REGION = US_EAST_1;

    @Nullable
    @Inject
    WebSocketConnectionRepository repository;

    private static final Logger LOG = LoggerFactory.getLogger(FunctionRequestHandler.class);

    public FunctionRequestHandler(ApplicationContextBuilder contextBuilder) {
        super(contextBuilder);
    }

    public FunctionRequestHandler() {
        super();
    }

    @Override
    public APIGatewayV2WebSocketResponse execute(APIGatewayV2WebSocketEvent input) {
        LOG.info("input {}", input);
        if (input != null) {
            processWebSocketEvent(input);
        }
        APIGatewayV2WebSocketResponse response = new APIGatewayV2WebSocketResponse();
        response.setStatusCode(200);
        return response;
    }

    void processWebSocketEvent(APIGatewayV2WebSocketEvent input) {
        if (repository != null) {
            EventType.of(input.getRequestContext().getEventType())
                    .ifPresent(eventType -> {
                        switch (eventType) {
                            case CONNECT:
                                userOfInput(input).ifPresent(user -> repository.save(user, connectionOfInput(input)));
                                break;
                            case DISCONNECT:
                                repository.delete(connectionOfInput(input));
                                break;
                            case MESSAGE:
                            default:
                                break;
                        }
                    });
        }
    }

    @NonNull
    private Optional<OAuthUser> userOfInput(@NonNull APIGatewayV2WebSocketEvent input) {
        if (input.getQueryStringParameters() == null) {
            return Optional.empty();
        }
        String token = input.getQueryStringParameters().get(QUERY_STRING_PARAM_TOKEN);
        return token == null ?
                Optional.empty() :
                OAuthUserUtils.userOfToken(token);
    }

    @NonNull
    private WebSocketConnection connectionOfInput(@NonNull APIGatewayV2WebSocketEvent input) {
        String region = System.getenv("AWS_REGION");
        if (region == null) {
            region = DEFAULT_REGION;
        }
        return new WebSocketConnection(region,
                input.getRequestContext().getApiId(),
                input.getRequestContext().getStage(),
                input.getRequestContext().getConnectionId(),
                input.getRequestContext().getDomainName());
    }
}

