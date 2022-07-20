package com.micronauttodo.persistence.dynamodb;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Introspected
public class AbstractItemKey implements CompositePrimaryKey, ItemKey {

    @NonNull
    private final String pk;

    @Nullable
    private final String sk;

    public AbstractItemKey(@NonNull String pk,
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

    @NonNull
    public Map<String, AttributeValue> toKey() {
        Map<String, AttributeValue> result = new HashMap<>();
        result.put(getPartitionKeyName(), s(getPartitionKey()));
        getSortKey().ifPresent(sortKey -> {
            result.put(getSortKeyName(), s(sortKey));
        });
        return result;
    }

    @NonNull
    private AttributeValue s(@NonNull String str) {
        return AttributeValue.builder().s(str).build();
    }
}
