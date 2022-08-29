package com.micronauttodo.conf;

import io.micronaut.context.BeanContext;
import io.micronaut.context.annotation.Property;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@Property(name = "websockets.url", value = "websockets.micronauttodo.com")
@MicronautTest(startApplication = false)
class WebsocketsConfigurationTest {

    @Inject
    BeanContext beanContext;

    @Test
    void beanOfTypeWebsocketConfiguration() {
        assertTrue(beanContext.containsBean(WebsocketsConfiguration.class));
    }

    @Test
    void immutableConfigurationViaProperty(WebsocketsConfiguration websocketsConfiguration) {
        assertEquals("websockets.micronauttodo.com", websocketsConfiguration.getUrl());
    }
}