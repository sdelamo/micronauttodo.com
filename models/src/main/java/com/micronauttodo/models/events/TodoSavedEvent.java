package com.micronauttodo.models.events;

import com.micronauttodo.models.OAuthUser;
import com.micronauttodo.models.Todo;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.NonNull;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Introspected
public class TodoSavedEvent {

    @NonNull
    @NotNull
    @Valid
    private final Todo todo;

    @NonNull
    @NotNull
    @Valid
    private final OAuthUser user;

    public TodoSavedEvent(@NonNull Todo todo,
                          @NonNull OAuthUser oAuthUser) {
        this.todo = todo;
        this.user = oAuthUser;
    }

    @NonNull
    public Todo getTodo() {
        return todo;
    }

    @NonNull
    public OAuthUser getUser() {
        return user;
    }
}
