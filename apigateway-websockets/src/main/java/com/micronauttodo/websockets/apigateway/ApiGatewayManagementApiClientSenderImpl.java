package com.micronauttodo.websockets.apigateway;

import com.micronauttodo.models.WebSocketConnection;
import com.micronauttodo.models.WebSocketMessage;
import io.micronaut.core.annotation.NonNull;
import jakarta.inject.Singleton;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.apigatewaymanagementapi.ApiGatewayManagementApiClient;
import software.amazon.awssdk.services.apigatewaymanagementapi.ApiGatewayManagementApiClientBuilder;
import software.amazon.awssdk.services.apigatewaymanagementapi.model.PostToConnectionRequest;
import software.amazon.awssdk.services.apigatewaymanagementapi.model.PostToConnectionResponse;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
public class ApiGatewayManagementApiClientSenderImpl implements ApiGatewayManagementApiClientSender {

    private final Map<WebSocketConnection, ApiGatewayManagementApiClient> clients = new ConcurrentHashMap<>();

    private final ApiGatewayManagementApiClientBuilder builder;

    public ApiGatewayManagementApiClientSenderImpl(ApiGatewayManagementApiClientBuilder builder) {
        this.builder = builder;
    }

    @Override
    @NonNull
    public PostToConnectionResponse send(@NonNull @NotNull @Valid WebSocketMessage webSocketMessage) {
        return send(webSocketMessage, SdkBytes.fromString(webSocketMessage.getMessage(), StandardCharsets.UTF_8));
    }

    @Override
    @NonNull
    public PostToConnectionResponse send(@NonNull @NotNull @Valid WebSocketConnection connection,
                                         @NonNull SdkBytes sdkBytes) {
        return clients.computeIfAbsent(connection, webSocketConnection -> builder
                        .endpointOverride(WebSocketConnectionUtils.uriOf(webSocketConnection))
                        .build())
                .postToConnection(PostToConnectionRequest.builder()
                        .data(sdkBytes)
                        .connectionId(connection.getConnectionId())
                        .build());
    }
}
