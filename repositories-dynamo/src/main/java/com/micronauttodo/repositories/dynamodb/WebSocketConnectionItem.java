package com.micronauttodo.repositories.dynamodb;

import com.micronauttodo.models.OAuthUser;
import com.micronauttodo.models.WebSocketConnection;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.NonNull;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.HashMap;
import java.util.Map;

import static com.micronauttodo.repositories.dynamodb.DynamoRepository.*;

@Introspected
public class WebSocketConnectionItem extends AbstractItem<WebSocketConnection> {
    public WebSocketConnectionItem(@NonNull WebSocketConnection webSocketConnection,
                                   @NonNull OAuthUser oAuthUser) {
        super(new WebSocketConnectionItemKey(webSocketConnection, oAuthUser), webSocketConnection);
    }

    @Override
    @NonNull
    public Map<String, AttributeValue> toItem() {
        Map<String, AttributeValue> item = super.toItem();
        item.putAll(gs2(getEntity()));
        return item;
    }

    public static Map<String, AttributeValue> gs2(@NonNull WebSocketConnection connection) {
        Map<String, AttributeValue> item = new HashMap<>();
        String pk = gs2Pk(connection);
        item.put(ATTRIBUTE_GSI_2_PK, s(pk));
        item.put(ATTRIBUTE_GSI_2_SK, s(pk));
        return item;
    }
    @NonNull
    public static String gs2Pk(@NonNull WebSocketConnection connection) {
        return WebSocketConnection.class.getSimpleName().toUpperCase() +
                HASH +
                connection.getConnectionId() +
                HASH +
                connection.getApiId() +
                HASH +
                connection.getDomainName();
    }
}
