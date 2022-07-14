package com.micronauttodo.persistence;

import io.micronaut.core.annotation.Creator;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Introspected
public class TodoCreate {

    @NonNull
    @NotBlank
    private final String task;

    @Creator
    public TodoCreate(@NonNull String task) {
        this.task = task;
    }

    @NonNull
    public String getTask() {
        return task;
    }
}
