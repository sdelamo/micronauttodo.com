package com.micronauttodo.persistence.dynamodb;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;

import java.util.Optional;

public interface CompositePrimaryKey {
    @NonNull
    default String getPartitionKeyName() {
        return "pk";
    }

    @NonNull
    default String getSortKeyName() {
        return "sk";
    }

    @NonNull
    String getPartitionKey();

    @NonNull
    Optional<String> getSortKey();
}
