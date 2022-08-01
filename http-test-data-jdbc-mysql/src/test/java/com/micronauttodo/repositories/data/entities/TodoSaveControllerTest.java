package com.micronauttodo.repositories.data.entities;

import com.micronauttodo.controllers.OauthUserArgumentBinder;
import com.micronauttodo.models.OAuthUser;
import com.micronauttodo.models.Todo;
import com.micronauttodo.repositories.TodoRepository;
import com.micronauttodo.repositories.UserRepository;
import io.micronaut.context.annotation.Property;
import io.micronaut.context.annotation.Replaces;
import io.micronaut.core.convert.ArgumentConversionContext;
import io.micronaut.core.util.StringUtils;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.client.BlockingHttpClient;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Property(name = "micronaut.security.filter.enabled", value = StringUtils.FALSE)
@Property(name = "micronaut.http.client.follow-redirects", value = StringUtils.FALSE)
@MicronautTest
@Testcontainers(disabledWithoutDocker = true)
class TodoSaveControllerTest {

    private static final OAuthUser OAUTHUSER = new OAuthUser("https://cognito-idp.us-east-1.amazonaws.com/us-east-1_abcdemu6o0#",
            "014e7c43-ff5c-23e7-4506-124fe64d2303",
            "john@email.com");

    @Inject
    UserRepository userRepository;

    @Inject
    TodoRepository todoRepository;

    @Client("/")
    @Inject
    HttpClient httpClient;

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

    @Replaces(OauthUserArgumentBinder.class)
    @Singleton
    static class OauthUserArgumentBinderReplacement extends OauthUserArgumentBinder {
        @Override
        public BindingResult<OAuthUser> bind(ArgumentConversionContext<OAuthUser> context, HttpRequest<?> source) {
            return () -> Optional.of(OAUTHUSER);
        }
    }
}