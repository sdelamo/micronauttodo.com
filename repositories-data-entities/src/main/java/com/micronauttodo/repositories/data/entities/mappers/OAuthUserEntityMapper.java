package com.micronauttodo.repositories.data.entities.mappers;

import com.micronauttodo.models.OAuthUser;
import com.micronauttodo.repositories.data.entities.OAuthUserEntity;
import com.micronauttodo.repositories.data.entities.OAuthUserId;
import io.micronaut.core.annotation.NonNull;

public final class OAuthUserEntityMapper {
    private OAuthUserEntityMapper() {
    }

    @NonNull
    public static OAuthUserEntity of(OAuthUser user) {
        return new OAuthUserEntity(new OAuthUserId(user.getIss(), user.getSub()), user.getEmail());
    }

}
