package com.micronauttodo.repositories.dynamodb;

import io.micronaut.core.annotation.NonNull;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.HashMap;
import java.util.Map;
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

    @NonNull
    default Map<String, AttributeValue> toKey() {
        Map<String, AttributeValue> result = new HashMap<>();
        result.put(getPartitionKeyName(), AttributeValue.builder().s(getPartitionKey()).build());
        getSortKey().ifPresent(sortKey -> {
            result.put(getSortKeyName(), AttributeValue.builder().s(sortKey).build());
        });
        return result;
    }
}
