package com.micronauttodo.repositories.dynamodb;

import com.micronauttodo.models.Todo;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.NonNull;

@Introspected
public class TodoItem extends AbstractItem<Todo> {

    public TodoItem(@NonNull ItemKey key,
                    @NonNull Todo todo) {
        super(key, todo);
    }
}
