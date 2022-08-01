package com.micronauttodo.repositories.dynamodb;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;

import java.util.Optional;

@Introspected
public class ItemKey implements CompositePrimaryKey {

    @NonNull
    private final String pk;

    @Nullable
    private final String sk;

    public ItemKey(@NonNull String pk,
                   @Nullable String sk) {
        this.pk = pk;
        this.sk = sk;

    }
    @Override
    @NonNull
    public String getPartitionKey() {
        return pk;
    }

    @Override
    @NonNull
    public Optional<String> getSortKey() {
        return Optional.ofNullable(sk);
    }
}
