package com.micronauttodo.repositories.dynamodb;

import com.micronauttodo.models.OAuthUser;
import com.micronauttodo.models.WebSocketConnection;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.NonNull;

@Introspected
public class WebSocketConnectionItem extends AbstractItem<WebSocketConnection> {
    public WebSocketConnectionItem(@NonNull WebSocketConnection webSocketConnection,
                                   @NonNull OAuthUser oAuthUser) {
        super(new WebSocketConnectionItemKey(webSocketConnection, oAuthUser), webSocketConnection);
    }
}
