package com.micronauttodo.persistence.dynamodb;

import com.micronauttodo.persistence.OAuthUser;
import com.micronauttodo.persistence.TodoCreate;
import com.micronauttodo.persistence.TodoRepository;
import com.micronauttodo.persistence.TodoSaveService;
import com.micronauttodo.persistence.UserRepository;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.util.CollectionUtils;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.test.support.TestPropertyProvider;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

@MicronautTest
@Testcontainers
@TestInstance(PER_CLASS)
class UserRepositoryDynamoTest implements TestPropertyProvider {

    @Inject
    UserRepository userRepository;

    @Inject
    TodoSaveService todoSaveService;

    @Inject
    TodoRepository todoRepository;

    @Container
    static GenericContainer dynamoDBLocal =
            new GenericContainer("amazon/dynamodb-local")
                    .withExposedPorts(8000);
    @Test
    void testTodoSave() {
        OAuthUser oAuthUser = new OAuthUser("https://cognito-idp.us-east-1.amazonaws.com/us-east-1_abcdemu6o0#",
                "014e7c43-ff5c-23e7-4506-124fe64d2303",
                "john@email.com");
        userRepository.save(oAuthUser);
        String todoId = todoSaveService.save(new TodoCreate("Clean"), oAuthUser);
        assertNotNull(todoId);
        assertEquals(1, todoRepository.findAll(oAuthUser).size());
        todoRepository.delete(todoId, oAuthUser);
        assertEquals(0, todoRepository.findAll(oAuthUser).size());
        userRepository.delete(oAuthUser);
    }

    @NonNull
    @Override
    public Map<String, String> getProperties() {
        if (!dynamoDBLocal.isRunning()) {
            dynamoDBLocal.start();
        }
        return CollectionUtils.mapOf(
                "dynamodb-local.host", "localhost",
                "dynamodb-local.port", dynamoDBLocal.getFirstMappedPort());
    }
}