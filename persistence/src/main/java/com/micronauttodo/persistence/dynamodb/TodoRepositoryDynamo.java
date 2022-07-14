package com.micronauttodo.persistence.dynamodb;

import com.micronauttodo.persistence.Identified;
import com.micronauttodo.persistence.OAuthUser;
import com.micronauttodo.persistence.Todo;
import com.micronauttodo.persistence.TodoRepository;
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
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Requires(beans = { DynamoConfiguration.class, DynamoDbClient.class })
@Singleton
public class TodoRepositoryDynamo extends DynamoRepository<Todo> implements TodoRepository  {
    private static final Logger LOG = LoggerFactory.getLogger(TodoRepositoryDynamo.class);

    private static final String ATTRIBUTE_TASK = "task";
    private static final String ATTRIBUTE_USER = "user";

    public TodoRepositoryDynamo(DynamoDbClient dynamoDbClient,
                                DynamoConfiguration dynamoConfiguration) {
        super(dynamoDbClient, dynamoConfiguration);
    }

    @Override
    public void delete(@NonNull @NotBlank String id,
                       @NonNull @NotNull @Valid OAuthUser user) {
        super.delete(pk(user, Todo.class, id));
    }

    @Override
    public void save(@NonNull @NotNull @Valid Todo todo,
                     @NonNull @NotNull @Valid OAuthUser user) {
        save(todo, todo1 -> item(todo1, user));
    }

    @NonNull
    protected Map<String, AttributeValue> item(@NonNull Todo todo, @NonNull OAuthUser user) {
        Map<String, AttributeValue> result = new HashMap<>();
        AttributeValue pk = pk(user, todo);
        result.put(ATTRIBUTE_PK, pk);
        result.put(ATTRIBUTE_SK, pk);
        result.put(ATTRIBUTE_GSI_1_PK, gs1Pk(todo));
        result.put(ATTRIBUTE_GSI_1_SK, pk);
        result.put(ATTRIBUTE_ID, s(todo.getId()));
        result.put(ATTRIBUTE_TASK, s(todo.getTask()));
        result.put(ATTRIBUTE_USER, m(UserRepositoryDynamo.itemForOauthUser(user)));
        if (LOG.isTraceEnabled()) {
            LOG.trace(ATTRIBUTE_PK + ": {}", result.get(ATTRIBUTE_PK).s());
            LOG.trace(ATTRIBUTE_SK + ": {}", result.get(ATTRIBUTE_SK).s());
            LOG.trace(ATTRIBUTE_GSI_1_PK + ": {}", result.get(ATTRIBUTE_GSI_1_PK).s());
            LOG.trace(ATTRIBUTE_GSI_1_SK + ": {}", result.get(ATTRIBUTE_GSI_1_SK).s());
            LOG.trace(ATTRIBUTE_ID + ": {}", result.get(ATTRIBUTE_ID).s());
            LOG.trace(ATTRIBUTE_TASK + ": {}", result.get(ATTRIBUTE_TASK).s());
        }
        return result;
     }

    @Override
    @NonNull
    public List<Todo> findAll(@NonNull @NotNull @Valid OAuthUser oAuthUser) {
        Class<?> cls = Todo.class;
        return findAll(cls, oAuthUser);
    }

    public List<Todo> findAll(@NonNull Class<?> cls,
                              @NonNull @NotNull @Valid OAuthUser oAuthUser) {
        List<Todo> result = new ArrayList<>();
        String beforeId = null;
        do {
            QueryRequest request = findAllQueryRequest(oAuthUser, cls, beforeId, null);
            QueryResponse response = dynamoDbClient.query(request);
            if (LOG.isTraceEnabled()) {
                LOG.trace(response.toString());
            }
            result.addAll(parseInResponse(response));
            beforeId = lastEvaluatedId(response, cls).orElse(null);
        } while(beforeId != null);

        if (LOG.isInfoEnabled()) {
            LOG.info("#{}: {}", cls.getSimpleName(), result.size());
        }
        return result;
    }

    @NonNull
    private List<Todo> parseInResponse(@NonNull QueryResponse response) {
        List<Map<String, AttributeValue>> items = response.items();
        List<Todo> result = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(items)) {
            for (Map<String, AttributeValue> item : items) {
                result.add(pojoOf(item));
            }
        }
        return result;
    }

    @NonNull
    private Todo pojoOf(@NonNull Map<String, AttributeValue> item) {
        return new Todo(item.get(ATTRIBUTE_ID).s(),
                item.get(ATTRIBUTE_TASK).s());
    }
}
