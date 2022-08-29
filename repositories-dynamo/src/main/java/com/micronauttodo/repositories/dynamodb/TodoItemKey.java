package com.micronauttodo.repositories.dynamodb;

import com.micronauttodo.models.OAuthUser;
import com.micronauttodo.models.Todo;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.NonNull;
import static com.micronauttodo.repositories.dynamodb.constants.DynamoDbConstants.*;

@Introspected
public class TodoItemKey extends ItemKey {

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
