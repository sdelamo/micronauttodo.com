package com.micronauttodo.repositories.dynamodb.constants;

import software.amazon.awssdk.services.dynamodb.model.AttributeDefinition;
import software.amazon.awssdk.services.dynamodb.model.BillingMode;
import software.amazon.awssdk.services.dynamodb.model.CreateTableRequest;
import software.amazon.awssdk.services.dynamodb.model.GlobalSecondaryIndex;
import software.amazon.awssdk.services.dynamodb.model.KeySchemaElement;
import software.amazon.awssdk.services.dynamodb.model.KeyType;
import software.amazon.awssdk.services.dynamodb.model.Projection;
import software.amazon.awssdk.services.dynamodb.model.ProjectionType;
import software.amazon.awssdk.services.dynamodb.model.ScalarAttributeType;

import java.util.Arrays;

public final class DynamoDbConstants {
    public static final String HASH = "#";
    public static final String ATTRIBUTE_PK = "pk";
    public static final String ATTRIBUTE_SK = "sk";
    public static final String ATTRIBUTE_ID = "id";
    public static final String ATTRIBUTE_GSI_1_PK = "GSI1PK";
    public static final String ATTRIBUTE_GSI_1_SK = "GSI1SK";
    public static final String INDEX_GSI_1 = "GSI1";

    public static final String ATTRIBUTE_GSI_2_PK = "GSI2PK";

    public static final String ATTRIBUTE_GSI_2_SK = "GSI2SK";

    public static final String INDEX_GSI_2 = "GSI2";

    private DynamoDbConstants() {

    }

    public static CreateTableRequest createTableRequest(String tableName) {
return CreateTableRequest.builder()
        .attributeDefinitions(AttributeDefinition.builder()
                        .attributeName(ATTRIBUTE_PK)
                        .attributeType(ScalarAttributeType.S)
                        .build(),
                AttributeDefinition.builder()
                        .attributeName(ATTRIBUTE_SK)
                        .attributeType(ScalarAttributeType.S)
                        .build(),
                AttributeDefinition.builder()
                        .attributeName(ATTRIBUTE_GSI_1_PK)
                        .attributeType(ScalarAttributeType.S)
                        .build(),
                AttributeDefinition.builder()
                        .attributeName(ATTRIBUTE_GSI_1_SK)
                        .attributeType(ScalarAttributeType.S)
                        .build(),
                AttributeDefinition.builder()
                        .attributeName(ATTRIBUTE_GSI_2_PK)
                        .attributeType(ScalarAttributeType.S)
                        .build(),
                AttributeDefinition.builder()
                        .attributeName(ATTRIBUTE_GSI_2_SK)
                        .attributeType(ScalarAttributeType.S)
                        .build())
        .keySchema(Arrays.asList(KeySchemaElement.builder()
                        .attributeName(ATTRIBUTE_PK)
                        .keyType(KeyType.HASH)
                        .build(),
                KeySchemaElement.builder()
                        .attributeName(ATTRIBUTE_SK)
                        .keyType(KeyType.RANGE)
                        .build()))
        .billingMode(BillingMode.PAY_PER_REQUEST)
        .tableName(tableName)
        .globalSecondaryIndexes(
                gsi(INDEX_GSI_1, ATTRIBUTE_GSI_1_PK, ATTRIBUTE_GSI_1_SK),
                gsi(INDEX_GSI_2, ATTRIBUTE_GSI_2_PK, ATTRIBUTE_GSI_2_SK)
        )
        .build();
    }

    private static GlobalSecondaryIndex gsi(String indexName,
                                            String pkAttributeName,
                                            String skAttributeName) {
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
