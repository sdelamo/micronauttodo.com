package com.micronauttodo.repositories;

import com.github.ksuid.Ksuid;
import com.micronauttodo.models.OAuthUser;
import com.micronauttodo.models.Todo;
import com.micronauttodo.repositories.microstream.TodoRepositoryImpl;
import io.micronaut.context.ApplicationContext;
import io.micronaut.core.util.CollectionUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TodoRepositoryTest {

    @TempDir
    static File tempDir;

    @Test
    void usersAreSavedWhenTheApplicationIsShutDown() {
        ApplicationContext context = createContext();
        TodoRepository repository = context.getBean(TodoRepository.class);
        OAuthUser user = new OAuthUser("https://cognito-idp.us-east-1.amazonaws.com/us-east-1_abcdemu6o0#",
                "014e7c43-ff5c-23e7-4506-124fe64d2303",
                "john@email.com");

        String todoId = id();
        repository.save(new Todo(todoId, "Learn Microstream"), user);

        assertEquals(1, ((TodoRepositoryImpl) repository).countByUser(user));

        context.close();
        context = createContext();
        repository = context.getBean(TodoRepository.class);

        assertEquals(1, ((TodoRepositoryImpl) repository).countByUser(user));

        String todoLearnJavaId = id();
        repository.save(new Todo(todoLearnJavaId, "Learn Java"), user);
        String todoLearnMicronautId = id();

        repository.save(new Todo(todoLearnMicronautId, "Learn Micronaut"), user);

        context.close();
        context = createContext();
        repository = context.getBean(TodoRepository.class);

        assertEquals(3, ((TodoRepositoryImpl) repository).countByUser(user));

        repository.delete(todoId, user);

        assertEquals(2, ((TodoRepositoryImpl) repository).countByUser(user));

        context.close();
        context = createContext();
        repository = context.getBean(TodoRepository.class);

        assertEquals(2, ((TodoRepositoryImpl) repository).countByUser(user));

        repository.delete(todoLearnJavaId, user);
        repository.delete(todoLearnMicronautId, user);

        context.close();
    }

    private static String id() {
        return Ksuid.newKsuid().toString();
    }

    ApplicationContext createContext() {
        return ApplicationContext.run(CollectionUtils.mapOf(
                "microstream.storage.todo.storage-directory", tempDir.getAbsolutePath(),
                "microstream.storage.todo.root-class", "com.micronauttodo.repositories.microstream.RootData"
        ));
    }
}
