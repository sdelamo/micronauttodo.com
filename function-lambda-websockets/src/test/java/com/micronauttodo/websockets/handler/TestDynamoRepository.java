package com.micronauttodo.websockets.handler;

import com.micronauttodo.repositories.dynamodb.DynamoConfiguration;
import io.micronaut.context.annotation.Requires;
import jakarta.inject.Singleton;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.DescribeTableRequest;
import software.amazon.awssdk.services.dynamodb.model.ResourceNotFoundException;

import static com.micronauttodo.repositories.dynamodb.constants.DynamoDbConstants.createTableRequest;

@Requires(beans = {DynamoDbClient.class, DynamoConfiguration.class})
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
