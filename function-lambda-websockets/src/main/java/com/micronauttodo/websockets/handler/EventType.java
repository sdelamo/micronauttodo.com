package com.micronauttodo.websockets.handler;

import io.micronaut.core.annotation.NonNull;

import java.util.Optional;

public enum EventType {
    CONNECT,
    MESSAGE,
    DISCONNECT;

    @NonNull
    public static Optional<EventType> of(@NonNull String str) {
        if (str.equalsIgnoreCase(CONNECT.toString())) {
            return Optional.of(CONNECT);
        } else if (str.equalsIgnoreCase(DISCONNECT.toString())) {
            return Optional.of(DISCONNECT);
        } else if (str.equalsIgnoreCase(MESSAGE.toString())) {
            return Optional.of(MESSAGE);
        }
        return Optional.empty();
    }
}
