package com.micronauttodo.repositories.data.entities;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.data.annotation.Relation;
import javax.validation.constraints.NotBlank;

@MappedEntity("todo")
public class TodoEntity {

    @Id
    @NonNull
    @NotBlank
    private final String id;

    @NonNull
    @NotBlank
    private final String task;

    @NonNull
    @Relation(Relation.Kind.MANY_TO_ONE)
    private final OAuthUserEntity user;

    public TodoEntity(@NonNull String id,
                      @NonNull String task,
                      @NonNull OAuthUserEntity user) {
        this.id = id;
        this.task = task;
        this.user = user;
    }

    @NonNull
    public String getId() {
        return id;
    }

    @NonNull
    public String getTask() {
        return task;
    }

    @NonNull
    public OAuthUserEntity getUser() {
        return user;
    }
}
