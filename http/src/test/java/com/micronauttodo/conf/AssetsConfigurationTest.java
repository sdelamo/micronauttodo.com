package com.micronauttodo.conf;

import io.micronaut.context.BeanContext;
import io.micronaut.context.annotation.Property;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@Property(name = "assets.url", value = "https://assets.micronauttodo.com")
@MicronautTest(startApplication = false)
class AssetsConfigurationTest {

    @Inject
    BeanContext beanContext;

    @Test
    void beanOfTypeAssetsConfiguration() {
        assertTrue(beanContext.containsBean(AssetsConfiguration.class));
    }

    @Test
    void immutableConfigurationViaProperty(AssetsConfiguration assetsConfiguration) {
        assertEquals("https://assets.micronauttodo.com", assetsConfiguration.getUrl());
    }
}