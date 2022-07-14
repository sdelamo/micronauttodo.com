package com.micronauttodo.controllers;

import com.micronauttodo.persistence.OAuthUser;
import com.micronauttodo.persistence.Todo;
import com.micronauttodo.persistence.TodoRepository;
import com.micronauttodo.persistence.TodoSaveService;
import com.micronauttodo.persistence.UserRepository;
import io.micronaut.context.annotation.Property;
import io.micronaut.context.annotation.Replaces;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.convert.ArgumentConversionContext;
import io.micronaut.core.type.Argument;
import io.micronaut.core.util.CollectionUtils;
import io.micronaut.core.util.StringUtils;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.bind.binders.TypedRequestArgumentBinder;
import io.micronaut.http.client.BlockingHttpClient;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.test.support.TestPropertyProvider;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

@Property(name = "micronaut.security.filter.enabled", value = StringUtils.FALSE)
@Property(name = "micronaut.http.client.follow-redirects", value = StringUtils.FALSE)
@MicronautTest
@Testcontainers
@TestInstance(PER_CLASS)
class TodoSaveControllerTest implements TestPropertyProvider {

    private static final OAuthUser OAUTHUSER = new OAuthUser("https://cognito-idp.us-east-1.amazonaws.com/us-east-1_abcdemu6o0#",
            "014e7c43-ff5c-23e7-4506-124fe64d2303",
            "john@email.com");

    @Inject
    UserRepository userRepository;

    @Inject
    TodoSaveService todoSaveService;

    @Inject
    TodoRepository todoRepository;

    @Client("/")
    @Inject
    HttpClient httpClient;

    @Container
    static GenericContainer dynamoDBLocal =
            new GenericContainer("amazon/dynamodb-local")
                    .withExposedPorts(8000);
    @Test
    void testTodoSave() {
        BlockingHttpClient client = httpClient.toBlocking();
        userRepository.save(OAUTHUSER);
        String task = "Clean";
        HttpRequest<?> request = HttpRequest.POST("/todo", Collections.singletonMap("task", task))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED_TYPE);
        HttpResponse<?> response = client.exchange(request);
        assertEquals(HttpStatus.SEE_OTHER, response.getStatus());
        assertEquals(HttpStatus.SEE_OTHER, response.getStatus());
        List<Todo> todoList = todoRepository.findAll(OAUTHUSER);
        assertEquals(1, todoList.size());
        assertEquals(task, todoList.get(0).getTask());
        todoRepository.delete(todoList.get(0).getId(), OAUTHUSER);
        assertEquals(0, todoRepository.findAll(OAUTHUSER).size());
        userRepository.delete(OAUTHUSER);
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

    @Replaces(OauthUserArgumentBinder.class)
    @Singleton
    static class OauthUserArgumentBinderReplacement extends OauthUserArgumentBinder {
        @Override
        public BindingResult<OAuthUser> bind(ArgumentConversionContext<OAuthUser> context, HttpRequest<?> source) {
            return () -> Optional.of(OAUTHUSER);
        }
    }
}