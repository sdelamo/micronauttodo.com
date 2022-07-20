package com.micronauttodo.models;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.NonNull;
import javax.validation.constraints.NotBlank;

@Introspected
public class Todo extends TodoCreate implements Identified {

    @NonNull
    @NotBlank
    private final String id;

    public Todo(@NonNull String id,
                @NonNull String task) {
        super(task);
        this.id = id;
    }

    @Override
    @NonNull
    public String getId() {
        return id;
    }
}
