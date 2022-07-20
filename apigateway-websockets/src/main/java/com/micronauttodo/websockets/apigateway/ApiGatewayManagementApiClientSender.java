package com.micronauttodo.websockets.apigateway;

import com.micronauttodo.models.WebSocketConnection;
import com.micronauttodo.models.WebSocketMessage;
import io.micronaut.core.annotation.NonNull;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.apigatewaymanagementapi.model.PostToConnectionResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public interface ApiGatewayManagementApiClientSender {

    @NonNull
    PostToConnectionResponse send(@NonNull @NotNull @Valid WebSocketMessage message);

    @NonNull
    PostToConnectionResponse send(@NonNull @NotNull @Valid WebSocketConnection connection,
                                  @NonNull SdkBytes sdkBytes);
}
