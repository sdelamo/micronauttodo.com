package com.micronauttodo.repositories.dynamodb;

import com.micronauttodo.models.OAuthUser;
import com.micronauttodo.models.WebSocketConnection;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.NonNull;

import java.util.Optional;

import static com.micronauttodo.repositories.dynamodb.DynamoRepository.HASH;

@Introspected
public class WebSocketConnectionItemKey implements CompositePrimaryKey {

    private final String pk;

    public WebSocketConnectionItemKey(@NonNull WebSocketConnection connection,
                                      @NonNull OAuthUser user) {
        this.pk = pk(connection, user);
    }
    public static String pk(@NonNull WebSocketConnection connection,
                            @NonNull OAuthUser user) {
        return pkPrefix(user) + connection.getConnectionId();
    }

    public static String pkPrefix(@NonNull OAuthUser user) {
        return UserItem.pkStr(user) + HASH + WebSocketConnection.class.getSimpleName().toUpperCase() + HASH;
    }

    @Override
    @NonNull
    public String getPartitionKey() {
        return pk;
    }

    @Override
    @NonNull
    public Optional<String> getSortKey() {
        return Optional.of(pk);
    }
}
