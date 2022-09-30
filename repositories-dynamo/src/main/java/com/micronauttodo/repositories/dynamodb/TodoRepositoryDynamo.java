package com.micronauttodo.repositories.dynamodb;

import com.micronauttodo.models.OAuthUser;
import com.micronauttodo.models.Todo;
import com.micronauttodo.repositories.TodoRepository;
import com.micronauttodo.repositories.dynamodb.constants.DynamoConfiguration;
import io.micronaut.context.annotation.Requires;
import io.micronaut.core.annotation.NonNull;
import jakarta.inject.Singleton;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;

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

    @Override
    @NonNull
    public Optional<Todo> findById(@NonNull @NotBlank String id,
                                   @NonNull @NotNull @Valid OAuthUser user) {
        return find(Todo.class, new TodoItemKey(id, user).toKey());
    }

    public List<Todo> findAll(@NonNull Class<?> cls,
                              @NonNull @NotNull @Valid OAuthUser user) {
        String pk = Item.getType(cls);
        String sk = TodoItemKey.pkPrefix(cls, user);
        return findAll(Todo.class, pk, sk);
    }
}
