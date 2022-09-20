package com.micronauttodo.repositories;

import com.micronauttodo.models.OAuthUser;
import com.micronauttodo.repositories.microstream.UserRepositoryImpl;
import io.micronaut.context.ApplicationContext;
import io.micronaut.core.util.CollectionUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserRepositoryTest {

    @TempDir
    static File tempDir;

    @Test
    void usersAreSavedWhenTheApplicationIsShutDown() {
        ApplicationContext context = createContext();
        UserRepository userRepository = context.getBean(UserRepository.class);
        OAuthUser oAuthUser = new OAuthUser("https://cognito-idp.us-east-1.amazonaws.com/us-east-1_abcdemu6o0#",
                "014e7c43-ff5c-23e7-4506-124fe64d2303",
                "john@email.com");
        userRepository.save(oAuthUser);

        assertEquals(1, ((UserRepositoryImpl) userRepository).count());

        context.close();

        context = createContext();

        userRepository = context.getBean(UserRepository.class);
        assertEquals(1, ((UserRepositoryImpl) userRepository).count());

        userRepository.delete(oAuthUser);

        assertEquals(0, ((UserRepositoryImpl) userRepository).count());

        context.close();
        context = createContext();

        userRepository = context.getBean(UserRepository.class);
        assertEquals(0, ((UserRepositoryImpl) userRepository).count());

        context.close();
    }

    ApplicationContext createContext() {
        return ApplicationContext.run(CollectionUtils.mapOf(
                "microstream.storage.todo.storage-directory", tempDir.getAbsolutePath(),
                "microstream.storage.todo.root-class", "com.micronauttodo.repositories.microstream.RootData"
        ));
    }
}
