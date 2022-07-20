package com.micronauttodo.repositories.dynamodb;

import io.micronaut.core.annotation.NonNull;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import java.util.Map;

public interface Item<T> {
    @NonNull
    Map<String, AttributeValue> toItem();

    @NonNull
    T getEntity();

    @NonNull
    Map<String, AttributeValue> toKey();

    @NonNull
    default String getType() {
        return getEntity().getClass().getSimpleName().toUpperCase();
    }

    static String getType(Class<?> cls) {
        return cls.getSimpleName().toUpperCase();
    }

    @NonNull
    default String getTypeName() {
        return "type";
    }
}
