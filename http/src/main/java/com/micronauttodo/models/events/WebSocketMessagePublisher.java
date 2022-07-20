package com.micronauttodo.models.events;

import com.micronauttodo.models.WebSocketMessage;
import io.micronaut.core.annotation.NonNull;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public interface WebSocketMessagePublisher {
    void publish(@NonNull @NotNull @Valid WebSocketMessage webSocketMessage);
}
