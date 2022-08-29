package com.micronauttodo.websockets.handler;

import org.junit.jupiter.api.Test;

import java.io.IOException;

class FunctionRequestHandlerEmptyConstructorTest {

    @Test
    void functionRequestHandlerHasAnEmptyConstructor() {
        try (FunctionRequestHandler handler = new FunctionRequestHandler()) {
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
