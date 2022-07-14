package com.micronauttodo.persistence.dynamodb;

import com.micronauttodo.persistence.Identified;
import com.micronauttodo.persistence.OAuthUser;
import com.micronauttodo.persistence.Todo;
import io.micronaut.context.annotation.Requires;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.core.util.CollectionUtils;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeDefinition;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.BillingMode;
import software.amazon.awssdk.services.dynamodb.model.CreateTableRequest;
import software.amazon.awssdk.services.dynamodb.model.DeleteItemRequest;
import software.amazon.awssdk.services.dynamodb.model.DeleteItemResponse;
import software.amazon.awssdk.services.dynamodb.model.DescribeTableRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemResponse;
import software.amazon.awssdk.services.dynamodb.model.GlobalSecondaryIndex;
import software.amazon.awssdk.services.dynamodb.model.KeySchemaElement;
import software.amazon.awssdk.services.dynamodb.model.KeyType;
import software.amazon.awssdk.services.dynamodb.model.Projection;
import software.amazon.awssdk.services.dynamodb.model.ProjectionType;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutItemResponse;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryResponse;
import software.amazon.awssdk.services.dynamodb.model.ResourceNotFoundException;
import software.amazon.awssdk.services.dynamodb.model.ScalarAttributeType;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public abstract class DynamoRepository<T> {
    private static final Logger LOG = LoggerFactory.getLogger(DynamoRepository.class);
    protected static final String HASH = "#";
    public static final String ATTRIBUTE_PK = "pk";
    public static final String ATTRIBUTE_SK = "sk";
    public static final String ATTRIBUTE_ID = "id";
    public static final String ATTRIBUTE_GSI_1_PK = "GSI1PK";
    public static final String ATTRIBUTE_GSI_1_SK = "GSI1SK";
    public static final String INDEX_GSI_1 = "GSI1";

    protected final DynamoDbClient dynamoDbClient;
    protected final DynamoConfiguration dynamoConfiguration;

    protected DynamoRepository(DynamoDbClient dynamoDbClient,
                            DynamoConfiguration dynamoConfiguration) {
        this.dynamoDbClient = dynamoDbClient;
        this.dynamoConfiguration = dynamoConfiguration;
    }

    protected void save(T entity, Function<T, Map<String, AttributeValue>> mapper) {
    PutItemResponse itemResponse = dynamoDbClient.putItem(PutItemRequest.builder()
            .tableName(dynamoConfiguration.getTableName())
            .item(mapper.apply(entity))
            .build());
        if (LOG.isDebugEnabled()) {
            LOG.debug(itemResponse.toString());
        }
    }

    @NonNull
    public QueryRequest findAllQueryRequest(@NonNull Class<?> cls,
                                            @Nullable String beforeId,
                                            @Nullable Integer limit) {
        QueryRequest.Builder builder = QueryRequest.builder()
                .tableName(dynamoConfiguration.getTableName())
                .indexName(INDEX_GSI_1)
                .scanIndexForward(false);
        if (limit != null) {
            builder.limit(limit);
        }
        if (beforeId == null) {
            return  builder.keyConditionExpression("#pk = :pk")
                    .expressionAttributeNames(Collections.singletonMap("#pk", ATTRIBUTE_GSI_1_PK))
                    .expressionAttributeValues(Collections.singletonMap(":pk",
                            classAttributeValue(cls)))
                    .build();
        } else {
            return builder.keyConditionExpression("#pk = :pk and #sk < :sk")
                    .expressionAttributeNames(CollectionUtils.mapOf("#pk", ATTRIBUTE_GSI_1_PK, "#sk", ATTRIBUTE_GSI_1_SK))
                    .expressionAttributeValues(CollectionUtils.mapOf(":pk",
                            classAttributeValue(cls),
                            ":sk",
                            id(cls, beforeId)
                    ))
                    .build();
        }
    }

    protected QueryRequest findAllQueryRequest(@NonNull OAuthUser oAuthUser,
                                               @NonNull Class<?> cls,
                                               @Nullable String beforeId,
                                               @Nullable Integer limit) {
        QueryRequest.Builder builder = QueryRequest.builder()
                .tableName(dynamoConfiguration.getTableName())
                .indexName(INDEX_GSI_1)
                .scanIndexForward(false);
        if (limit != null) {
            builder.limit(limit);
        }
        String pk = cls.getSimpleName().toUpperCase();
        String sk = pkPrefix(oAuthUser, cls);
        if (LOG.isTraceEnabled()) {
            LOG.trace("pk {} sk: {}", pk, sk);
        }
        return builder.keyConditionExpression(
                beforeId == null ?
                "#pk = :pk and begins_with(#sk,:sk)" :
                "#pk = :pk and begins_with(#sk,:sk) and #sk < :sk")
                .expressionAttributeNames(Map.of("#pk", ATTRIBUTE_GSI_1_PK, "#sk", ATTRIBUTE_GSI_1_SK))
                .expressionAttributeValues(Map.of(":pk", s(pk), ":sk", s(sk)))
                .build();
    }

    protected void delete(@NonNull @NotNull Class<?> cls, @NonNull @NotBlank String id) {
        AttributeValue pk = id(cls, id);
        delete(pk, pk);
    }

    protected void delete(@NonNull @NotNull AttributeValue pk,
                          @NonNull @NotNull AttributeValue sk) {
        DeleteItemResponse deleteItemResponse = dynamoDbClient.deleteItem(DeleteItemRequest.builder()
                .tableName(dynamoConfiguration.getTableName())
                .key(CollectionUtils.mapOf(ATTRIBUTE_PK, pk, ATTRIBUTE_SK, sk))
                .build());
        if (LOG.isDebugEnabled()) {
            LOG.debug(deleteItemResponse.toString());
        }
    }

    protected Optional<Map<String, AttributeValue>> findById(@NonNull @NotNull Class<?> cls, @NonNull @NotBlank String id) {
        AttributeValue pk = id(cls, id);
        GetItemResponse getItemResponse = dynamoDbClient.getItem(GetItemRequest.builder()
                .tableName(dynamoConfiguration.getTableName())
                .key(CollectionUtils.mapOf(ATTRIBUTE_PK, pk, ATTRIBUTE_SK, pk))
                .build());
        return !getItemResponse.hasItem() ? Optional.empty() : Optional.of(getItemResponse.item());
    }

    @NonNull
    public static Optional<String> lastEvaluatedId(@NonNull QueryResponse response,
                                          @NonNull Class<?> cls) {
        if (response.hasLastEvaluatedKey()) {
            Map<String, AttributeValue> item = response.lastEvaluatedKey();
            if (item != null && item.containsKey(ATTRIBUTE_PK)) {
                return id(cls, item.get(ATTRIBUTE_PK));
            }
        }
        return Optional.empty();
    }

    @NonNull
    protected Map<String, AttributeValue> item(@NonNull T entity) {
        Map<String, AttributeValue> item = new HashMap<>();
        item.put(ATTRIBUTE_PK, pk(entity));
        item.put(ATTRIBUTE_SK, sk(entity));
        item.put(ATTRIBUTE_GSI_1_PK, gs1Pk(entity));
        item.put(ATTRIBUTE_GSI_1_SK, gs1Sk(entity));
        return item;
    }

    @NonNull
    protected AttributeValue gs1Pk(@NonNull T entity) {
        return classAttributeValue(entity.getClass());
    }

    @NonNull
    protected AttributeValue gs1Sk(@NonNull T entity) {
        return pk(entity);
    }

    @NonNull
    protected AttributeValue sk(@NonNull T entity) {
        return pk(entity);
    }

    @NonNull
    protected AttributeValue pk(@NonNull T entity) {
        if (entity instanceof Identified) {
            return id(entity.getClass(), ((Identified) entity).getId());
        }
        throw new UnsupportedOperationException();
    }

    @NonNull
    protected AttributeValue s(String str) {
        return AttributeValue.builder().s(str).build();
    }

    @NonNull
    protected AttributeValue m(Map<String, AttributeValue> m) {
        return AttributeValue.builder().m(m).build();
    }

    protected String pkPrefix(@NonNull OAuthUser oAuthUser,
                              @NonNull Class<?> entity) {
        return UserRepositoryDynamo.pkStr(oAuthUser) +
                HASH +
                entity.getSimpleName().toUpperCase() +
                HASH;
    }

    protected String pkStr(@NonNull OAuthUser oAuthUser,
                           @NonNull Class<?> entity,
                           @NonNull String id) {
        return pkPrefix(oAuthUser, entity) + id;
    }

    protected AttributeValue pk(@NonNull OAuthUser oAuthUser,
                              @NonNull Class<?> entity,
                              @NonNull String id) {
        return AttributeValue.builder()
                .s(pkStr(oAuthUser, entity, id))
                .build();
    }

    protected AttributeValue pk(@NonNull OAuthUser oAuthUser,
                                @NonNull Identified identified) {
        return pk(oAuthUser, identified.getClass(), identified.getId());
    }

    @NonNull
    public static AttributeValue classAttributeValue(@NonNull Class<?> cls) {
        return AttributeValue.builder()
                .s(cls.getSimpleName().toUpperCase())
                .build();
    }

    @NonNull
    protected static AttributeValue id(@NonNull Class<?> cls,
                                     @NonNull String id) {
        return AttributeValue.builder()
                .s(prefix(cls) + id)
                .build();
    }

    public static String prefix(@NonNull Class<?> cls) {
        return cls.getSimpleName().toUpperCase() + HASH;
    }

    @NonNull
    protected static Optional<String> id(@NonNull Class<?> cls,
                                       @NonNull AttributeValue attributeValue) {
        String str = attributeValue.s();
        String substring = cls.getSimpleName().toUpperCase() + HASH;
        return str.startsWith(substring) ? Optional.of(str.substring(substring.length())) : Optional.empty();
    }

    @NonNull
    protected AttributeValue convert(@NonNull LocalDate localDate) {
        return AttributeValue.builder().s(localDate.toString()).build();
    }

    @NonNull
    protected Optional<LocalDate> unconvert(@NonNull Map<String, AttributeValue> item, @NonNull String key) {
        if (item.containsKey(key)) {
            AttributeValue attributeValue = item.get(key);
            return unconvert(attributeValue);
        }
        return Optional.empty();
    }

    @NonNull
    protected Optional<LocalDate> unconvert(@NonNull AttributeValue item) {
        try {
            return Optional.of(unconvert(item.s()));
        } catch (DateTimeParseException e) {
            return Optional.empty();
        }
    }

    protected static LocalDate unconvert(String s) {
        return LocalDate.parse(s);
    }

    protected void delete(@NonNull AttributeValue pk) {
        DeleteItemResponse deleteItemResponse = dynamoDbClient.deleteItem(DeleteItemRequest.builder()
                .tableName(dynamoConfiguration.getTableName())
                .key(CollectionUtils.mapOf(ATTRIBUTE_PK, pk, ATTRIBUTE_SK, pk))
                .build());
        if (LOG.isDebugEnabled()) {
            LOG.debug(deleteItemResponse.toString());
        }
    }
}