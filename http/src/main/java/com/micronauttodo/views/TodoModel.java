package com.micronauttodo.views;

import com.micronauttodo.models.Todo;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.NonNull;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Introspected
public class TodoModel extends Model {
    @NonNull
    @NotBlank
    private final String accessToken;

    @NonNull
    @NotNull
    private final List<Todo> todos;

    public TodoModel(String host,
                     String accessToken,
                     List<Todo> todos) {
        super(host);
        this.accessToken = accessToken;
        this.todos = todos;
    }

    @NonNull
    public String getAccessToken() {
        return accessToken;
    }

    @NonNull
    public List<Todo> getTodos() {
        return todos;
    }
}
