package com.micronauttodo.views;

import com.micronauttodo.models.Todo;
import io.micronaut.core.annotation.Introspected;
import java.util.List;

@Introspected
public class TodoModel extends Model {
    private final List<Todo> todos;

    public TodoModel(List<Todo> todos) {
        this.todos = todos;
    }

    public List<Todo> getTodos() {
        return todos;
    }
}
