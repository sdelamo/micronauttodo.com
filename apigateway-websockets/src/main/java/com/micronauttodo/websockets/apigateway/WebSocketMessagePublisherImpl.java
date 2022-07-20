package com.micronauttodo.websockets.apigateway;

import com.micronauttodo.models.events.WebSocketMessagePublisher;
import com.micronauttodo.models.WebSocketMessage;
import io.micronaut.core.annotation.NonNull;
import jakarta.inject.Singleton;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Singleton
public class WebSocketMessagePublisherImpl implements WebSocketMessagePublisher {

    private final ApiGatewayManagementApiClientSender sender;

    public WebSocketMessagePublisherImpl(ApiGatewayManagementApiClientSender sender) {
        this.sender = sender;
    }

    @Override
    public void publish(@NonNull @NotNull @Valid WebSocketMessage message) {
        sender.send(message);
    }
}
