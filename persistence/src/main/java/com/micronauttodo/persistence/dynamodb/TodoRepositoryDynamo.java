package com.micronauttodo.persistence.dynamodb;

import com.micronauttodo.persistence.OAuthUser;
import com.micronauttodo.persistence.Todo;
import com.micronauttodo.persistence.TodoRepository;
import io.micronaut.context.annotation.Requires;
import io.micronaut.core.annotation.NonNull;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Requires(beans = { DynamoConfiguration.class, DynamoDbClient.class })
@Singleton
public class TodoRepositoryDynamo extends DynamoRepository implements TodoRepository  {
    public TodoRepositoryDynamo(DynamoDbClient dynamoDbClient,
                                DynamoConfiguration dynamoConfiguration) {
        super(dynamoDbClient, dynamoConfiguration);
    }

    @Override
    public void delete(@NonNull @NotBlank String id,
                       @NonNull @NotNull @Valid OAuthUser user) {
        super.delete(new TodoItemKey(id, user).toKey());
    }

    @Override
    public void save(@NonNull @NotNull @Valid Todo todo,
                     @NonNull @NotNull @Valid OAuthUser user) {
        save(new TodoItem(new TodoItemKey(todo, user), todo).toItem());
    }

    @Override
    @NonNull
    public List<Todo> findAll(@NonNull @NotNull @Valid OAuthUser oAuthUser) {
        Class<?> cls = Todo.class;
        return findAll(cls, oAuthUser);
    }

    public List<Todo> findAll(@NonNull Class<?> cls,
                              @NonNull @NotNull @Valid OAuthUser user) {
        String pk = Item.getType(cls);
        String sk = TodoItemKey.pkPrefix(cls, user);
        return findAll(Todo.class, pk, sk);
    }
}
