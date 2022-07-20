package com.micronauttodo.models;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;

import javax.validation.constraints.NotBlank;

@Introspected
public class WebSocketMessage extends WebSocketConnection {
    @NonNull
    @NotBlank
    private final String message;

    public WebSocketMessage(@NonNull WebSocketConnection webSocketConnection,
                            @NonNull String message) {
        this(webSocketConnection.getRegion(),
                webSocketConnection.getApiId(),
                webSocketConnection.getStage(),
                webSocketConnection.getConnectionId(),
                webSocketConnection.getDomainName(),
                message);
    }

    public WebSocketMessage(@NonNull String region,
                            @NonNull String apiId,
                            @NonNull String stage,
                            @NonNull String connectionId,
                            @Nullable String domainName,
                            @NonNull String message) {
        super(region, apiId, stage, connectionId, domainName);
        this.message = message;
    }

    @NonNull
    public String getMessage() {
        return message;
    }
}
