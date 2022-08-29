package com.micronauttodo.repositories.dynamodb;

import com.micronauttodo.models.OAuthUser;
import com.micronauttodo.models.WebSocketConnection;
import com.micronauttodo.repositories.WebSocketConnectionRepository;
import io.micronaut.context.annotation.Requires;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.util.CollectionUtils;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryResponse;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;
import static com.micronauttodo.repositories.dynamodb.constants.DynamoDbConstants.*;
@Requires(beans = { DynamoDbClient.class, DynamoConfiguration.class })
@Singleton
public class WebSocketConnectionRepositoryDynamo extends DynamoRepository
        implements WebSocketConnectionRepository {
    private static final Logger LOG = LoggerFactory.getLogger(WebSocketConnectionRepositoryDynamo.class);

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
    public void delete(@NonNull @NotNull @Valid WebSocketConnection connection) {
        String pk = WebSocketConnectionItem.gs2Pk(connection);
        QueryRequest request = findAllQueryRequest(pk, pk, INDEX_GSI_2, ATTRIBUTE_GSI_2_PK, ATTRIBUTE_GSI_2_SK,null, 1);
        QueryResponse response = dynamoDbClient.query(request);
        if (LOG.isTraceEnabled()) {
            LOG.trace(response.toString());
        }
        if (response.hasItems()) {
            for (Map<String, AttributeValue> item : response.items()) {
                if (item.containsKey(ATTRIBUTE_PK) && item.containsKey(ATTRIBUTE_SK)) {
                    delete(CollectionUtils.mapOf(ATTRIBUTE_PK, item.get(ATTRIBUTE_PK), ATTRIBUTE_SK, item.get(ATTRIBUTE_SK)));
                }
            }
        }
    }

    @Override
    @NonNull
    public List<WebSocketConnection> findAllByUser(@NonNull @NotNull @Valid OAuthUser user) {
        return findAll(WebSocketConnection.class,
                Item.getType(WebSocketConnection.class),
                WebSocketConnectionItemKey.pkPrefix(user));
    }
}
