package com.micronauttodo.websockets.apigateway;

import com.micronauttodo.models.WebSocketConnection;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.http.uri.UriBuilder;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.net.URI;

public final class WebSocketConnectionUtils {
    private WebSocketConnectionUtils() {

    }

    @NonNull
    public static URI uriOf(@NonNull @NotNull @Valid WebSocketConnection webSocketConnection) {
        return UriBuilder.of("https://" + webSocketConnection.getApiId() + ".execute-api.us-east-1.amazonaws.com")
                .path(webSocketConnection.getStage())
                .build();
    }
}
