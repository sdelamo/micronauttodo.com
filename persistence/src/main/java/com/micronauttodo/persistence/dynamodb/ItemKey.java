package com.micronauttodo.persistence.dynamodb;

import io.micronaut.core.annotation.NonNull;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.Map;

public interface ItemKey extends CompositePrimaryKey {

    @NonNull
    Map<String, AttributeValue> toKey();
}
