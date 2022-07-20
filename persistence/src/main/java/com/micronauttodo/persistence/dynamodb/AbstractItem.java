package com.micronauttodo.persistence.dynamodb;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.core.beans.BeanProperty;
import io.micronaut.core.beans.BeanWrapper;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.HashMap;
import java.util.Map;

import static com.micronauttodo.persistence.dynamodb.DynamoRepository.ATTRIBUTE_GSI_1_PK;
import static com.micronauttodo.persistence.dynamodb.DynamoRepository.ATTRIBUTE_GSI_1_SK;

public abstract class AbstractItem<T> extends AbstractItemKey implements Item {
    private final T entity;
    public AbstractItem(@NonNull String pk,
                        @Nullable String sk,
                        @NonNull T entity) {
        super(pk, sk);
        this.entity = entity;
    }

    public AbstractItem(@NonNull ItemKey itemKey,
                        @NonNull T entity) {
        super(itemKey.getPartitionKey(), itemKey.getSortKey().orElse(null));
        this.entity = entity;
    }

    @NonNull
    @Override
    public Map<String, AttributeValue> toItem() {
        Map<String, AttributeValue> result = toKey();
        result.put(getTypeName(), s(getType()));
        result.put(ATTRIBUTE_GSI_1_PK, s(getType()));
        result.put(ATTRIBUTE_GSI_1_SK, result.get(getPartitionKeyName()));
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
    private AttributeValue s(@NonNull String str) {
        return AttributeValue.builder().s(str).build();
    }

    @NonNull
    private AttributeValue n(@NonNull String str) {
        return AttributeValue.builder().n(str).build();
    }
}
