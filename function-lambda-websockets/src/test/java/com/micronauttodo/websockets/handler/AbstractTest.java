package com.micronauttodo.websockets.handler;

import io.micronaut.context.ApplicationContextBuilder;
import io.micronaut.core.util.CollectionUtils;
import io.micronaut.function.aws.LambdaApplicationContextBuilder;
import org.testcontainers.containers.GenericContainer;

import java.util.Map;

public class AbstractTest {
    GenericContainer dynamoDBLocal;

    GenericContainer getDynamoDbLocal() {
        if (dynamoDBLocal == null) {
            dynamoDBLocal =
                    new GenericContainer("amazon/dynamodb-local")
                            .withExposedPorts(8000);
            if (!dynamoDBLocal.isRunning()) {
                dynamoDBLocal.start();
            }
        }
        return dynamoDBLocal;
    }

    Map<String, Object> getDynamoDbLocalProperties() {
        return CollectionUtils.mapOf(
                "dynamodb-local.host", "localhost",
                "dynamodb-local.port", getDynamoDbLocal().getFirstMappedPort(),
                "dynamodb.table-name", "todo"
        );
    }

    ApplicationContextBuilder getApplicationContextBuilder() {
        Map<String, Object> properties = getDynamoDbLocalProperties();
        ApplicationContextBuilder contextBuilder = new LambdaApplicationContextBuilder();
        contextBuilder.properties(properties);
        return contextBuilder;
    }

    FunctionRequestHandler getHandler() {
        FunctionRequestHandler handler = new FunctionRequestHandler(getApplicationContextBuilder());
        TestDynamoRepository dynamoRepository = handler.getApplicationContext().getBean(TestDynamoRepository.class);
        if (!dynamoRepository.existsTable()) {
            dynamoRepository.createTable();
        }
        return handler;
    }
}
