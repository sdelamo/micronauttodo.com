package com.micronauttodo.persistence.dynamodb;

import com.micronauttodo.persistence.OAuthUser;
import com.micronauttodo.persistence.Todo;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;

import static com.micronauttodo.persistence.dynamodb.DynamoRepository.HASH;

@Introspected
public class TodoItemKey extends AbstractItemKey {

    public TodoItemKey(@NonNull Todo todo,
                       @NonNull OAuthUser user) {
        this(todo.getId(), user);
    }

    public TodoItemKey(@NonNull String id,
                       @NonNull OAuthUser user) {
        super(pk(id, Todo.class, user), pk(id, Todo.class, user));
    }

    public TodoItemKey(@NonNull String id,
                       @NonNull Class<?> cls,
                       @NonNull OAuthUser user) {
        super(pk(id, cls, user), pk(id, cls, user));
    }

    private static String pk(@NonNull String id,
                            @NonNull Class<?> cls,
                            @NonNull OAuthUser user) {
        return pkPrefix(cls, user) + id;
    }

    public static String pkPrefix(@NonNull Class<?> cls,
                                @NonNull OAuthUser user) {
        return UserItem.pkStr(user) + HASH + cls.getSimpleName().toUpperCase() + HASH;
    }
}
