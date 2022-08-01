package com.micronauttodo.repositories.dynamodb;

import com.micronauttodo.models.OAuthUser;
import com.micronauttodo.models.WebSocketConnection;
import com.micronauttodo.repositories.WebSocketConnectionRepository;
import io.micronaut.core.annotation.NonNull;
import jakarta.inject.Singleton;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@Singleton
public class WebSocketConnectionRepositoryDynamo extends DynamoRepository
        implements WebSocketConnectionRepository {

    protected WebSocketConnectionRepositoryDynamo(DynamoDbClient dynamoDbClient,
                                                  DynamoConfiguration dynamoConfiguration) {
        super(dynamoDbClient, dynamoConfiguration);
    }

    @Override
    public void save(@NonNull @NotNull OAuthUser user,
                     @NonNull @NotNull @Valid WebSocketConnection connection) {
        save(new WebSocketConnectionItem(connection, user).toItem());
    }

    @Override
    public void delete(@NonNull @NotNull OAuthUser user,
                       @NonNull @NotNull @Valid WebSocketConnection connection) {
        delete(new WebSocketConnectionItemKey(connection, user).toKey());
    }

    @Override
    @NonNull
    public List<WebSocketConnection> findAllByUser(@NonNull @NotNull @Valid OAuthUser user) {
        return findAll(WebSocketConnection.class,
                Item.getType(WebSocketConnection.class),
                WebSocketConnectionItemKey.pkPrefix(user));
    }
}
