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
import static com.micronauttodo.repositories.dynamodb.constants.DynamoDbConstants.createTableRequest;

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
        dynamoDbClient.createTable(createTableRequest(dynamoConfiguration.getTableName()));
    }
}
