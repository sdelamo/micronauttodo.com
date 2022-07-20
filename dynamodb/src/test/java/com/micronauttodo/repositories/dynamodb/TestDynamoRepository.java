package com.micronauttodo.repositories.dynamodb;

import io.micronaut.core.annotation.NonNull;
import jakarta.inject.Singleton;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeDefinition;
import software.amazon.awssdk.services.dynamodb.model.BillingMode;
import software.amazon.awssdk.services.dynamodb.model.CreateTableRequest;
import software.amazon.awssdk.services.dynamodb.model.DescribeTableRequest;
import software.amazon.awssdk.services.dynamodb.model.GlobalSecondaryIndex;
import software.amazon.awssdk.services.dynamodb.model.KeySchemaElement;
import software.amazon.awssdk.services.dynamodb.model.KeyType;
import software.amazon.awssdk.services.dynamodb.model.Projection;
import software.amazon.awssdk.services.dynamodb.model.ProjectionType;
import software.amazon.awssdk.services.dynamodb.model.ResourceNotFoundException;
import software.amazon.awssdk.services.dynamodb.model.ScalarAttributeType;

import java.util.Arrays;

import static com.micronauttodo.repositories.dynamodb.DynamoRepository.*;

@Singleton
public class TestDynamoRepository {

    protected final DynamoDbClient dynamoDbClient;
    protected final DynamoConfiguration dynamoConfiguration;

    public TestDynamoRepository(DynamoDbClient dynamoDbClient,
                            DynamoConfiguration dynamoConfiguration) {
        this.dynamoDbClient = dynamoDbClient;
        this.dynamoConfiguration = dynamoConfiguration;
    }


    public boolean existsTable() {
        try {
            dynamoDbClient.describeTable(DescribeTableRequest.builder()
                    .tableName(dynamoConfiguration.getTableName())
                    .build());
            return true;
        } catch (ResourceNotFoundException e) {
            return false;
        }
    }

    public void createTable() {
        dynamoDbClient.createTable(CreateTableRequest.builder()
                .attributeDefinitions(AttributeDefinition.builder()
                                .attributeName(DynamoRepository.ATTRIBUTE_PK)
                                .attributeType(ScalarAttributeType.S)
                                .build(),
                        AttributeDefinition.builder()
                                .attributeName(DynamoRepository.ATTRIBUTE_SK)
                                .attributeType(ScalarAttributeType.S)
                                .build(),
                        AttributeDefinition.builder()
                                .attributeName(ATTRIBUTE_GSI_1_PK)
                                .attributeType(ScalarAttributeType.S)
                                .build(),
                        AttributeDefinition.builder()
                                .attributeName(ATTRIBUTE_GSI_1_SK)
                                .attributeType(ScalarAttributeType.S)
                                .build())
                .keySchema(Arrays.asList(KeySchemaElement.builder()
                                .attributeName(DynamoRepository.ATTRIBUTE_PK)
                                .keyType(KeyType.HASH)
                                .build(),
                        KeySchemaElement.builder()
                                .attributeName(DynamoRepository.ATTRIBUTE_SK)
                                .keyType(KeyType.RANGE)
                                .build()))
                .billingMode(BillingMode.PAY_PER_REQUEST)
                .tableName(dynamoConfiguration.getTableName())
                .globalSecondaryIndexes(gsi(INDEX_GSI_1, ATTRIBUTE_GSI_1_PK, ATTRIBUTE_GSI_1_SK))
                .build());
    }
    private static GlobalSecondaryIndex gsi(@NonNull String indexName,
                                            @NonNull String pkAttributeName,
                                            @NonNull String skAttributeName) {
        return GlobalSecondaryIndex.builder()
                .indexName(indexName)
                .keySchema(KeySchemaElement.builder()
                        .attributeName(pkAttributeName)
                        .keyType(KeyType.HASH)
                        .build(), KeySchemaElement.builder()
                        .attributeName(skAttributeName)
                        .keyType(KeyType.RANGE)
                        .build())
                .projection(Projection.builder()
                        .projectionType(ProjectionType.ALL)
                        .build())
                .build();
    }

}
