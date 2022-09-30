package com.micronauttodo.localstack;

import com.micronauttodo.repositories.dynamodb.constants.DynamoConfiguration;
import com.micronauttodo.repositories.dynamodb.constants.DynamoDbConstants;
import jakarta.inject.Singleton;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.DescribeTableRequest;
import software.amazon.awssdk.services.dynamodb.model.ResourceNotFoundException;

@Singleton
public class DevDynamoRepository {

    protected final DynamoDbClient dynamoDbClient;
    protected final DynamoConfiguration dynamoConfiguration;

    public DevDynamoRepository(DynamoDbClient dynamoDbClient,
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
        dynamoDbClient.createTable(DynamoDbConstants.createTableRequest(dynamoConfiguration.getTableName()));
    }
}
