package com.micronauttodo.repositories.data.entities;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.data.annotation.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import javax.validation.constraints.Email;

@Table(name = "user")
@Entity
public class OAuthUserEntity {

    @EmbeddedId
    @NonNull
    private final OAuthUserId id;

    @NonNull
    @Email
    private final String email;

    public OAuthUserEntity(@NonNull OAuthUserId id,
                           @NonNull String email) {
        this.id = id;
        this.email = email;
    }

    @NonNull
    public OAuthUserId getId() {
        return id;
    }

    @NonNull
    public String getEmail() {
        return email;
    }
}
