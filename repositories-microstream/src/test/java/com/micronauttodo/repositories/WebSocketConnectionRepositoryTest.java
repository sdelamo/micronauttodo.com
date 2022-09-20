package com.micronauttodo.repositories;

import com.micronauttodo.models.OAuthUser;
import com.micronauttodo.models.WebSocketConnection;
import com.micronauttodo.repositories.microstream.WebSocketConnectionRepositoryImpl;
import io.micronaut.context.ApplicationContext;
import io.micronaut.core.util.CollectionUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;

class WebSocketConnectionRepositoryTest {

    @TempDir
    static File tempDir;

    @Test
    void usersAreSavedWhenTheApplicationIsShutDown() {
        ApplicationContext context = createContext();
        WebSocketConnectionRepository userRepository = context.getBean(WebSocketConnectionRepository.class);
        OAuthUser oAuthUser = new OAuthUser("https://cognito-idp.us-east-1.amazonaws.com/us-east-1_abcdemu6o0#",
                "014e7c43-ff5c-23e7-4506-124fe64d2303",
                "john@email.com");
        WebSocketConnection connection = new WebSocketConnection("region",
                "apiId",
                "stage",
                "connectionId",
                "String domainName");
        userRepository.save(oAuthUser, connection);

        assertEquals(1, ((WebSocketConnectionRepositoryImpl) userRepository).countByUser(oAuthUser));

        context.close();
        context = createContext();
        userRepository = context.getBean(WebSocketConnectionRepository.class);

        assertEquals(1, ((WebSocketConnectionRepositoryImpl) userRepository).countByUser(oAuthUser));
        userRepository.delete(connection);

        assertEquals(0, ((WebSocketConnectionRepositoryImpl) userRepository).countByUser(oAuthUser));

        context.close();
        context = createContext();
        userRepository = context.getBean(WebSocketConnectionRepository.class);

        assertEquals(0, ((WebSocketConnectionRepositoryImpl) userRepository).countByUser(oAuthUser));

        context.close();
    }

    ApplicationContext createContext() {
        return ApplicationContext.run(CollectionUtils.mapOf(
                "microstream.storage.todo.storage-directory", tempDir.getAbsolutePath(),
                "microstream.storage.todo.root-class", "com.micronauttodo.repositories.microstream.RootData"
        ));
    }
}
