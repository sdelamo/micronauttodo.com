package com.micronauttodo.websockets.handler;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EventTypeTest {

    @Test
    void toStringEventType() {
        assertEquals("DISCONNECT", EventType.DISCONNECT.toString());
        assertEquals("CONNECT", EventType.CONNECT.toString());
    }
}