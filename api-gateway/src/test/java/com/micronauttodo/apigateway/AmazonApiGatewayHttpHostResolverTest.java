package com.micronauttodo.apigateway;

import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@MicronautTest(startApplication = false)
class AmazonApiGatewayHttpHostResolverTest {

    @Inject
    AmazonApiGatewayHttpHostResolver amazonApiGatewayHttpHostResolver;

    @Test
    void testBaseURLForAmazonApiGateway() {
        assertTrue(amazonApiGatewayHttpHostResolver.isAmazonApiGatewayHost("https://x6vd5dxda0.execute-api.us-east-2.amazonaws.com"));
        assertFalse(amazonApiGatewayHttpHostResolver.isAmazonApiGatewayHost("https://customdomain.com"));
    }

}