package com.micronauttodo.repositories.dynamodb;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.beans.BeanProperty;
import io.micronaut.core.beans.BeanWrapper;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.Map;
import static com.micronauttodo.repositories.dynamodb.constants.DynamoDbConstants.*;

public abstract class AbstractItem<T> implements Item {
    private final T entity;
    private final CompositePrimaryKey key;
    public AbstractItem(@NonNull CompositePrimaryKey key,
                        @NonNull T entity) {
        this.key = key;
        this.entity = entity;
    }

    @Override
    @NonNull
    public Map<String, AttributeValue> toKey() {
        return key.toKey();
    }

    @Override
    @NonNull
    public Map<String, AttributeValue> toItem() {
        Map<String, AttributeValue> result = toKey();
        result.put(getTypeName(), s(getType()));
        result.put(ATTRIBUTE_GSI_1_PK, s(getType()));
        result.put(ATTRIBUTE_GSI_1_SK, result.get(key.getPartitionKeyName()));
        BeanWrapper<T> wrapper = BeanWrapper.getWrapper(entity);
        for (BeanProperty<T, ?> beanProperty : wrapper.getBeanProperties()) {
            if (CharSequence.class.isAssignableFrom(beanProperty.getType())) {
                beanProperty.get(entity, CharSequence.class).ifPresent(value -> {
                    result.put(beanProperty.getName(), s(value.toString()));
                });
            } else if (Number.class.isAssignableFrom(beanProperty.getType())) {
                beanProperty.get(entity, Number.class).ifPresent(value -> {
                    result.put(beanProperty.getName(), n(value.toString()));
                });
            }
        }
        return result;
    }

    @Override
    @NonNull
    public T getEntity() {
        return entity;
    }

    @NonNull
    public static AttributeValue s(@NonNull String str) {
        return AttributeValue.builder().s(str).build();
    }

    @NonNull
    private AttributeValue n(@NonNull String str) {
        return AttributeValue.builder().n(str).build();
    }
}
